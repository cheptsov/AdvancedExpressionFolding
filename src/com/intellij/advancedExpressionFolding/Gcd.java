package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Gcd extends Function implements ArithmeticExpression {
    public Gcd(List<Expression> operands) {
        super("gcd", operands);
    }

    @Override
    protected Gcd copy(List<Expression> newOperands) {
        return new Gcd(newOperands);
    }
}
