package com.intellij.advancedExpressionFolding;

public class VariableDeclaration extends Variable {
    private final String type;

    public VariableDeclaration(String type, String name) {
        super(name);
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
