package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Cos extends Function implements ArithmeticExpression {
    public Cos(List<Expression> operands) {
        super("cos", operands);
    }

    @Override
    protected Cos copy(List<Expression> newOperands) {
        return new Cos(newOperands);
    }
}
