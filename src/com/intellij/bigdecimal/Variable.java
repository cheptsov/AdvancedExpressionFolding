package com.intellij.bigdecimal;

public class Variable extends Expression {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String format() {
        return name;
    }

    public String getName() {
        return name;
    }
}
