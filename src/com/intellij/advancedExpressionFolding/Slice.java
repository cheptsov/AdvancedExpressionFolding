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

public class Slice extends Function implements SlicingExpression {
    public Slice(TextRange textRange, List<Expression> operands) {
        super(textRange, "slice", operands);
    }

    @Override
    protected Slice copy(List<Expression> newOperands) {
        return new Slice(textRange, newOperands);
    }

    @Override
    public String format() {
        String q = operands.get(0).format();
        String r1 = operands.get(1) instanceof NumberLiteral &&
                ((NumberLiteral) operands.get(1)).getNumber().intValue() == 0 ?
                "" : operands.get(1).format();
        String r2 = operands.size() == 2 ? "" : operands.get(2).format();
        return q + "[" + r1 + ":" + r2 + "]";
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && operands.stream().allMatch(e -> e.getTextRange() != null);
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Slice.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(operands.get(0).getTextRange().getEndOffset(),
                        operands.get(1) instanceof NumberLiteral
                                && ((NumberLiteral) operands.get(1)).getNumber().intValue() == 0
                                    ? operands.get(1).getTextRange().getEndOffset()
                                    : operands.get(1).getTextRange().getStartOffset()
                ), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "[";
            }
        });
        if (operands.get(1) instanceof NumberLiteral
                && ((NumberLiteral) operands.get(1)).getNumber().intValue() < 0
                && document.getText(TextRange.create(operands.get(1).getTextRange().getStartOffset() + 1,
                operands.get(1).getTextRange().getStartOffset() + 2)).equals(" ")) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(operands.get(1).getTextRange().getStartOffset() + 1,
                            operands.get(1).getTextRange().getStartOffset() + 2), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        if (operands.size() > 2) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(operands.get(1).getTextRange().getEndOffset(),
                            operands.get(2).getTextRange().getStartOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return ":";
                }
            });
            if (operands.get(2) instanceof NumberLiteral
                    && ((NumberLiteral) operands.get(2)).getNumber().intValue() < 0
                    && document.getText(TextRange.create(operands.get(2).getTextRange().getStartOffset() + 1,
                    operands.get(2).getTextRange().getStartOffset() + 2)).equals(" ")) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(operands.get(2).getTextRange().getStartOffset() + 1,
                                operands.get(2).getTextRange().getStartOffset() + 2), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                });
            }
        }
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(
                        operands.size() > 2
                        ? getTextRange().getEndOffset() - 1
                        : operands.get(1).getTextRange().getEndOffset(), getTextRange().getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return operands.size() == 2 ? ":]" : "]";
            }
        });
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, false)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(element, document));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
