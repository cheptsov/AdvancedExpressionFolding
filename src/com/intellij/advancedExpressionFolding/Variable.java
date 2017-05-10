package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class Variable extends Expression {
    protected @NotNull String name;
    protected boolean copy;

    public Variable(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String name) {
        super(element, textRange);
        this.name = name;
        this.copy = false;
    }

    public Variable(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String name, boolean copy) {
        super(element, textRange);
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
        return true;
    }

    public boolean isCopy() {
        return copy;
    }
}
