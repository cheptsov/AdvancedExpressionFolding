package com.intellij.advancedExpressionFolding;

import java.util.List;

public class And extends Operation implements ArithmeticExpression {
    public And(List<Expression> operands) {
        super("&", 50, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new And(operands);
    }
}
