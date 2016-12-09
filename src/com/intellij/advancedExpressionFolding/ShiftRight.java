package com.intellij.advancedExpressionFolding;

import java.util.List;

public class ShiftRight extends Operation implements ArithmeticExpression {
    public ShiftRight(List<Expression> operands) {
        super(">>", 20, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftRight(operands);
    }
}
