package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Sinh extends Function implements ArithmeticExpression {
    public Sinh(List<Expression> operands) {
        super("sinh", operands);
    }

    @Override
    protected Sinh copy(List<Expression> newOperands) {
        return new Sinh(newOperands);
    }
}
