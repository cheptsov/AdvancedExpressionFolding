package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Function extends Expression {
    private final @NotNull String name;
    protected final @NotNull List<Expression> operands;

    public Function(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String name, @NotNull List<Expression> operands) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Function function = (Function) o;

        return name.equals(function.name) && operands.equals(function.operands);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operands.hashCode();
        return result;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return (operands.size() == 1 || operands.size() == 2 && operands.get(0).getTextRange().getStartOffset() < operands.get(1).getTextRange().getStartOffset()) && getTextRange().getStartOffset() < operands.get(0).getTextRange().getStartOffset();
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(getTextRange().getStartOffset(),
                        operands.get(0).getTextRange().getStartOffset()), group) {
            @NotNull
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
                    @NotNull
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
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return ")";
                }
            });
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
