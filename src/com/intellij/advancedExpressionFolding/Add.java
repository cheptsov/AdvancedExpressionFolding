package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Add extends Operation implements ArithmeticExpression {
    public Add(List<Expression> operands) {
        super("+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Add(operands);
    }
}
