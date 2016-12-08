package com.intellij.bigdecimal;

public class NumberLiteral extends Expression {
    private Number number;

    public NumberLiteral(Number number) {
        this.number = number;
    }

    @Override
    public String format() {
        String format = format(number.doubleValue());
        return format != null ? format : number.toString();
    }

    public Number getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberLiteral numberLiteral = (NumberLiteral) o;

        return number.toString().equals(numberLiteral.number.toString());
    }

    @Override
    public int hashCode() {
        return number.toString().hashCode();
    }
}
