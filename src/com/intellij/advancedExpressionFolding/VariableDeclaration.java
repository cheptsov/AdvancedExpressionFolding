package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

public class VariableDeclaration extends Variable {
    private final String type;

    public VariableDeclaration(TextRange textRange, String type, String name) {
        super(textRange, name);
        this.type = type;
    }

    @Override
    public String format() {
        return type + " " + getName();
    }

    public String getType() {
        return type;
    }
}
