package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class NumberLiteral extends Expression {
    private @NotNull Number number;

    public NumberLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Number number) {
        super(element, textRange);
        this.number = number;
    }

    @NotNull
    public Number getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberLiteral numberLiteral = (NumberLiteral) o;

        return number.toString().equals(numberLiteral.number.toString());
    }

    @Override
    public int hashCode() {
        return number.toString().hashCode();
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return true;
    }
}
