package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Variable extends Expression implements HighlightingExpression, ArithmeticExpression, ConcatenationExpression {
    private @NotNull String name;
    private boolean copy;
    private @Nullable TextRange variableTextRange;

    public Variable(@NotNull PsiElement element, @NotNull TextRange textRange, @Nullable TextRange variableTextRange, @NotNull String name) {
        super(element, textRange);
        this.variableTextRange = variableTextRange;
        this.name = name;
        this.copy = false;
    }

    public Variable(@NotNull PsiElement element, @NotNull TextRange textRange, @Nullable TextRange variableTextRange, @NotNull String name, boolean copy) {
        super(element, textRange);
        this.variableTextRange = variableTextRange;
        this.name = name;
        this.copy = copy;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return variableTextRange != null && variableTextRange.getStartOffset() > textRange.getStartOffset()
                && variableTextRange.getEndOffset() < textRange.getEndOffset();
        // TODO: Support inclusive ranges
    }

    public boolean isCopy() {
        return copy;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        //noinspection Duplicates
        if (variableTextRange != null) {
            FoldingGroup group = FoldingGroup
                    .newGroup(Variable.class.getName() + HighlightingExpression.GROUP_POSTFIX);
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(textRange.getStartOffset(), variableTextRange.getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(variableTextRange.getEndOffset(), textRange.getEndOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isCollapsedByDefault() {
        // TODO: Depend on the type (String or Number)
        return super.isCollapsedByDefault();
    }
}
