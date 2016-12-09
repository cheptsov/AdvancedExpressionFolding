package com.intellij.advancedExpressionFolding;

import java.util.List;

public class ShiftRightAssign extends Operation implements ArithmeticExpression {
    public ShiftRightAssign(List<Expression> operands) {
        super(">>=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftRightAssign(newOperands);
    }
}
