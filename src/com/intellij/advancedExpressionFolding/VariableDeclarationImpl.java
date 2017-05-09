package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

public class VariableDeclarationImpl extends Expression implements VariableDeclaration {
    private final boolean isFinal;

    public VariableDeclarationImpl(PsiElement element, TextRange textRange, boolean isFinal) {
        super(element, textRange);
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
