package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Ulp extends Function implements ArithmeticExpression {
    public Ulp(List<Expression> operands) {
        super("ulp", operands);
    }

    @Override
    protected Ulp copy(List<Expression> newOperands) {
        return new Ulp(newOperands);
    }
}
