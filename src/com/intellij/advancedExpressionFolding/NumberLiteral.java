package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class NumberLiteral extends Expression {
    private Number number;

    public NumberLiteral(PsiElement element, TextRange textRange, Number number) {
        super(element, textRange);
        this.number = number;
    }

    @Override
    public String format() {
        String format = format(number.doubleValue());
        return format != null ? format : number.toString();
    }

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
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        return FoldingDescriptor.EMPTY;
    }
}
