package com.intellij.bigdecimal;

import java.util.List;

public abstract class Expression {
    public Expression simplify() {
        return this;
    }

    protected Expression copy(List<Expression> newOperands) {
        throw new UnsupportedOperationException();
    }

    public abstract String format();
}
