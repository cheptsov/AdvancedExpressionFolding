package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Ceil extends Function implements ArithmeticExpression {
    public Ceil(List<Expression> operands) {
        super("ceil", operands);
    }

    @Override
    protected Ceil copy(List<Expression> newOperands) {
        return new Ceil(newOperands);
    }
}
