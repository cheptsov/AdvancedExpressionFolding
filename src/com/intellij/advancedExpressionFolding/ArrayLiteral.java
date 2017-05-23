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

public class ArrayLiteral extends Expression implements GetExpression {
    private final List<Expression> items;

    public ArrayLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expression> items) {
        super(element, textRange);
        this.items = items;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return items.stream().allMatch(i -> true);
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(ArrayLiteral.class.getName());
        if (items.isEmpty()) {
            return new FoldingDescriptor[] {
                    new FoldingDescriptor(element.getNode(), textRange,
                            group) {
                        @NotNull
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
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "[";
                }
            });
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(
                    items.get(items.size() - 1).getTextRange().getEndOffset(),
                    textRange.getEndOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "]";
                }
            });
            for (Expression item : items) {
                if (item.supportsFoldRegions(document, this)) {
                    Collections.addAll(descriptors, item.buildFoldRegions(item.getElement(), document, this));
                }
            }
            return descriptors.toArray(new FoldingDescriptor[0]);
        }
    }
}
