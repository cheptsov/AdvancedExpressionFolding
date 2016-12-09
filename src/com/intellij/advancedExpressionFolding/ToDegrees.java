package com.intellij.advancedExpressionFolding;

import java.util.List;

public class ToDegrees extends Function implements ArithmeticExpression {
    public ToDegrees(List<Expression> operands) {
        super("toDegrees", operands);
    }

    @Override
    protected ToDegrees copy(List<Expression> newOperands) {
        return new ToDegrees(newOperands);
    }
}
