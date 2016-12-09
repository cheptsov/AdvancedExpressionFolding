package com.intellij.advancedExpressionFolding;

import java.util.List;

public class MultiplyAssign extends Operation implements ArithmeticExpression {
    public MultiplyAssign(List<Expression> operands) {
        super("*=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new MultiplyAssign(newOperands);
    }
}
