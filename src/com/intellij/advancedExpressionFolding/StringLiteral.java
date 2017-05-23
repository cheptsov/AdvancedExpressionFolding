package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringLiteral extends Expression {
    private @NotNull String string;

    public StringLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String string) {
        super(element, textRange);
        this.string = string;
    }

    @NotNull
    public String getString() {
        return string;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }
}
