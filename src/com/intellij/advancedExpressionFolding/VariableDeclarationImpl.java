package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class VariableDeclarationImpl extends Expression implements VariableDeclaration {
    private final boolean isFinal;

    public VariableDeclarationImpl(TextRange textRange, boolean isFinal) {
        super(textRange);
        this.isFinal = isFinal;
    }

    @Override
    public String format() {
        return isFinal ? "val" : "var";
    }

    public boolean isFinal() {
        return isFinal;
    }
}
