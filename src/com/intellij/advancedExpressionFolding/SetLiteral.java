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

public class SetLiteral extends Function {
    @NotNull
    private final TextRange firstBracesRange;
    @NotNull
    private final TextRange secondBracesRange;

    public SetLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull TextRange firstBracesRange,
                      @NotNull TextRange secondBracesRange, @NotNull List<Expression> items) {
        super(element, textRange, "Set.of", items);
        this.firstBracesRange = firstBracesRange;
        this.secondBracesRange = secondBracesRange;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        int offset = getTextRange().getStartOffset();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(offset, firstBracesRange.getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "[";
            }
        });
        if (firstBracesRange.getStartOffset() < secondBracesRange.getStartOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(firstBracesRange.getStartOffset(), secondBracesRange.getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        if (secondBracesRange.getStartOffset() < operands.get(0).getTextRange().getStartOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(secondBracesRange.getStartOffset(),
                            operands.get(0).getTextRange().getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        offset = operands.get(0).getTextRange().getEndOffset();
        for (int i = 1; i < operands.size(); i++) {
            TextRange r = TextRange.create(offset, operands.get(i).getTextRange().getStartOffset());
            String p = ", ";
            if (!document.getText(r).equals(p)) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        r, group) {
                    @Override
                    public String getPlaceholderText() {
                        return p;
                    }
                });
            }
            offset = operands.get(i).getTextRange().getEndOffset();
        }
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(offset, secondBracesRange.getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        if (secondBracesRange.getEndOffset() < firstBracesRange.getEndOffset() - 1) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(secondBracesRange.getEndOffset(), firstBracesRange.getEndOffset() - 1), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(firstBracesRange.getEndOffset() - 1, firstBracesRange.getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "]";
            }
        });
        if (firstBracesRange.getEndOffset() < getTextRange().getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(firstBracesRange.getEndOffset(), getTextRange().getEndOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, this)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(operand.getElement(), document, this));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @NotNull
    public TextRange getFirstBracesRange() {
        return firstBracesRange;
    }

    @NotNull
    public TextRange getSecondBracesRange() {
        return secondBracesRange;
    }
}
