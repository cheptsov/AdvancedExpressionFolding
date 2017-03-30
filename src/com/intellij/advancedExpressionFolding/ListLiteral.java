package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListLiteral extends Expression implements GetExpression {
    private final List<Expression> items;

    public ListLiteral(PsiElement element, TextRange textRange, List<Expression> items) {
        super(element, textRange);
        this.items = items;
    }

    @Override
    public String format() {
        return "[" + String.join(", ", items.stream().map(Expression::format).collect(Collectors.toList())) + "]";
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null && items.stream().allMatch(i -> i.getTextRange() != null);
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(ListLiteral.class.getName());
        if (items.isEmpty()) {
            return new FoldingDescriptor[] {
                    new FoldingDescriptor(element.getNode(), textRange,
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "[]";
                        }
                    }
            };
        } else {
            ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset(),
                    items.get(0).getTextRange().getStartOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return "[";
                }
            });
            if (items.get(items.size() - 1).getTextRange().getEndOffset() < textRange.getEndOffset()) {
                descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(
                        items.get(items.size() - 1).getTextRange().getEndOffset(),
                        textRange.getEndOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "]";
                    }
                });
            }
            for (Expression item : items) {
                Collections.addAll(descriptors, item.buildFoldRegions(item.getElement(), document));
            }
            return descriptors.toArray(new FoldingDescriptor[0]);
        }
    }
}
