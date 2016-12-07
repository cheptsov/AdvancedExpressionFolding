package com.intellij.bigdecimal;

public class Literal extends Expression {
    private Number number;

    public Literal(Number number) {
        this.number = number;
    }

    @Override
    public String format() {
        return number.toString();
    }

    public Number getNumber() {
        return number;
    }
}
