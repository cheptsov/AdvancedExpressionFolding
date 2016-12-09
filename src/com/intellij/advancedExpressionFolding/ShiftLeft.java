package com.intellij.advancedExpressionFolding;

import java.util.List;

public class ShiftLeft extends Operation implements ArithmeticExpression {
    public ShiftLeft(List<Expression> operands) {
        super("<<", 20, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftLeft(operands);
    }
}
