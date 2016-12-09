package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Rint extends Function implements ArithmeticExpression {
    public Rint(List<Expression> operands) {
        super("rint", operands);
    }

    @Override
    protected Rint copy(List<Expression> newOperands) {
        return new Rint(newOperands);
    }
}
