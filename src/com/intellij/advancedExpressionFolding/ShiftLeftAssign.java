package com.intellij.advancedExpressionFolding;

import java.util.List;

public class ShiftLeftAssign extends Operation implements ArithmeticExpression {
    public ShiftLeftAssign(List<Expression> operands) {
        super("<<=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftLeftAssign(newOperands);
    }
}
