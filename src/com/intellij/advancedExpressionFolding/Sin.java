package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Sin extends Function implements ArithmeticExpression {
    public Sin(List<Expression> operands) {
        super("sin", operands);
    }

    @Override
    protected Sin copy(List<Expression> newOperands) {
        return new Sin(newOperands);
    }
}
