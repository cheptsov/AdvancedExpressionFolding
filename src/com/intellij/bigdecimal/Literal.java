package com.intellij.bigdecimal;

import java.math.BigDecimal;

public class Literal extends Expression {
    private BigDecimal number;

    public Literal(BigDecimal number) {
        this.number = number;
    }

    @Override
    public String format() {
        return number.toString();
    }

    public BigDecimal getNumber() {
        return number;
    }
}
