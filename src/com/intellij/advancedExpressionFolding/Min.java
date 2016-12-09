package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Min extends Function implements ArithmeticExpression {
    public Min(List<Expression> operands) {
        super("min", operands);
    }

    @Override
    protected Min copy(List<Expression> newOperands) {
        return new Min(newOperands);
    }
}
