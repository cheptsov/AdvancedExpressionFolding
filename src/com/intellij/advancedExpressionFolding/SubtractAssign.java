package com.intellij.advancedExpressionFolding;

import java.util.List;

public class SubtractAssign extends Operation implements ArithmeticExpression {
    public SubtractAssign(List<Expression> operands) {
        super("-=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new SubtractAssign(newOperands);
    }
}
