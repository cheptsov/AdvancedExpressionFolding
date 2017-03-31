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

public abstract class Function extends Expression {
    private final String name;
    protected final List<Expression> operands;

    public Function(PsiElement element, TextRange textRange, String name, List<Expression> operands) {
        super(element, textRange);
        this.name = name;
        this.operands = operands;
    }

    @Override
    public boolean isCollapsedByDefault() {
        for (Expression operand : operands) {
            if (!operand.isCollapsedByDefault()) {
                return false;
            }
        }
        return super.isCollapsedByDefault();
    }

    @Override
    public String format() {
        return name + "(" + operands.stream().map(Expression::format).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public Function simplify(boolean compute) {
        List<Expression> simplifiedOperands = null;
        for (int i = 0; i < operands.size(); i++) {
            Expression operand = operands.get(i);
            Expression simplifiedOperand = operand.simplify(compute);
            if (simplifiedOperand != operand) {
                if (simplifiedOperands == null) {
                    simplifiedOperands = new ArrayList<>();
                    if (i > 0) {
                        simplifiedOperands.addAll(operands.subList(0, i));
                    }
                }
                simplifiedOperands.add(simplifiedOperand);
            } else {
                if (simplifiedOperands != null) {
                    simplifiedOperands.add(operand);
                }
            }
        }
        return simplifiedOperands != null ? (Function) copy(simplifiedOperands) : this;
    }

    abstract Expression copy(List<Expression> newOperands);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Function function = (Function) o;

        if (!name.equals(function.name)) return false;
        return operands.equals(function.operands);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operands.hashCode();
        return result;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && (operands.size() == 1
                || operands.size() == 2
                    && operands.get(0).getTextRange().getStartOffset() < operands.get(1).getTextRange().getStartOffset())
                && getTextRange().getStartOffset() < operands.get(0).getTextRange().getStartOffset();
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(getTextRange().getStartOffset(),
                        operands.get(0).getTextRange().getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return name + "(";
            }
        });
        if (operands.get(0).supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, operands.get(0).buildFoldRegions(operands.get(0).getElement(), document));
        }
        if (operands.size() == 2) {
            TextRange commaOffset = TextRange.create(operands.get(0).getTextRange().getEndOffset(),
                    operands.get(1).getTextRange().getStartOffset());
            if (", ".equals(document.getText(commaOffset))) {
                descriptors.add(new FoldingDescriptor(element.getNode(), commaOffset, group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return ", ";
                    }
                });
                if (operands.get(1).supportsFoldRegions(document, false)) {
                    Collections.addAll(descriptors, operands.get(1).buildFoldRegions(operands.get(1).getElement(), document));
                }
            }
        }
        if (operands.get(operands.size() - 1).getTextRange().getEndOffset() < getTextRange().getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(operands.get(operands.size() - 1).getTextRange().getEndOffset(),
                            getTextRange().getEndOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return ")";
                }
            });
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
