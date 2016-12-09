package com.intellij.advancedExpressionFolding;

import java.util.List;

public class AndAssign extends Operation implements ArithmeticExpression {
    public AndAssign(List<Expression> operands) {
        super("&=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new AndAssign(newOperands);
    }
}
