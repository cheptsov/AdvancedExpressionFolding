package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Random extends Function implements ArithmeticExpression {
    public Random(List<Expression> operands) {
        super("random", operands);
    }

    @Override
    protected Random copy(List<Expression> newOperands) {
        return new Random(newOperands);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
