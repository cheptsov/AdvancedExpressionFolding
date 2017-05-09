package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class Variable extends Expression {
    protected String name;
    protected boolean copy;

    public Variable(PsiElement element, TextRange textRange, String name) {
        super(element, textRange);
        this.name = name;
        this.copy = false;
    }

    public Variable(PsiElement element, TextRange textRange, String name, boolean copy) {
        super(element, textRange);
        this.name = name;
        this.copy = copy;
    }

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
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        return FoldingDescriptor.EMPTY;
    }

    public boolean isCopy() {
        return copy;
    }
}
