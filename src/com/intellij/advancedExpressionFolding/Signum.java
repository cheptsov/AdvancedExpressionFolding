package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Signum extends Function implements ArithmeticExpression {
    public Signum(List<Expression> operands) {
        super("signum", operands);
    }

    @Override
    protected Signum copy(List<Expression> newOperands) {
        return new Signum(newOperands);
    }
}
