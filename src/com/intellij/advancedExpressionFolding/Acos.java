package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Acos extends Function implements ArithmeticExpression {
    public Acos(List<Expression> operands) {
        super("acos", operands);
    }

    @Override
    protected Acos copy(List<Expression> newOperands) {
        return new Acos(newOperands);
    }
}
