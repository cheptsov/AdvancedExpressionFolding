package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class StringLiteral extends Expression {
    private String string;

    public StringLiteral(TextRange textRange, String string) {
        super(textRange);
        this.string = string;
    }

    @Override
    public String format() {
        return "\"" + string + "\"";
    }

    public String getString() {
        return string;
    }
}
