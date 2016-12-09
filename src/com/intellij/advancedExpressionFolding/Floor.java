package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Floor extends Function implements ArithmeticExpression {
    public Floor(List<Expression> operands) {
        super("floor", operands);
    }

    @Override
    protected Floor copy(List<Expression> newOperands) {
        return new Floor(newOperands);
    }
}
