package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class Variable extends Expression {
    protected String name;

    public Variable(TextRange textRange, String name) {
        super(textRange);
        this.name = name;
    }

    @Override
    public String format() {
        return name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
