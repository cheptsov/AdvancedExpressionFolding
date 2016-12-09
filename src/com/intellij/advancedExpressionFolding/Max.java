package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Max extends Function implements ArithmeticExpression {
    public Max(List<Expression> operands) {
        super("max", operands);
    }

    @Override
    protected Max copy(List<Expression> newOperands) {
        return new Max(newOperands);
    }
}
