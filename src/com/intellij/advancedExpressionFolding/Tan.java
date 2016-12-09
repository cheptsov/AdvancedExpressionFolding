package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Tan extends Function implements ArithmeticExpression {
    public Tan(List<Expression> operands) {
        super("tan", operands);
    }

    @Override
    protected Tan copy(List<Expression> newOperands) {
        return new Tan(newOperands);
    }
}
