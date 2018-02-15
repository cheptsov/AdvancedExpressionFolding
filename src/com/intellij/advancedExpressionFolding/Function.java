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

public abstract class Function extends Expression {
    protected final @NotNull
    List<Expression> operands;
    private final @NotNull
    String name;

    public Function(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String name,
                    @NotNull List<Expression> operands) {
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
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        // TODO: check if operands have text in between
        return operands.size() > 0
                && getTextRange().getStartOffset() < operands.get(0).getTextRange().getStartOffset()
                && getTextRange().getEndOffset() > operands.get(operands.size() - 1).getTextRange().getEndOffset();
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        int offset = getTextRange().getStartOffset();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(offset, operands.get(0).getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return name + "(";
            }
        });
        offset = operands.get(0).getTextRange().getEndOffset();
        //noinspection Duplicates
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
                TextRange.create(offset, getTextRange().getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return ")";
            }
        });
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, this)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(operand.getElement(), document, this));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @NotNull
    public List<Expression> getOperands() {
        return operands;
    }
}
