package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Cosh extends Function implements ArithmeticExpression {
    public Cosh(List<Expression> operands) {
        super("cosh", operands);
    }

    @Override
    protected Cosh copy(List<Expression> newOperands) {
        return new Cosh(newOperands);
    }
}
