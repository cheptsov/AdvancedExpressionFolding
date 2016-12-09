package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Tanh extends Function implements ArithmeticExpression {
    public Tanh(List<Expression> operands) {
        super("tanh", operands);
    }

    @Override
    protected Tanh copy(List<Expression> newOperands) {
        return new Tanh(newOperands);
    }
}
