package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class StringLiteral extends Expression {
    private String string;

    public StringLiteral(PsiElement element, TextRange textRange, String string) {
        super(element, textRange);
        this.string = string;
    }

    @Override
    public String format() {
        return "\"" + string + "\"";
    }

    public String getString() {
        return string;
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
