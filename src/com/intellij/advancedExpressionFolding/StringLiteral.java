package com.intellij.advancedExpressionFolding;

public class StringLiteral extends Expression {
    private String string;

    public StringLiteral(String string) {
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
