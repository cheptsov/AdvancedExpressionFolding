package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Asin extends Function implements ArithmeticExpression {
    public Asin(List<Expression> operands) {
        super("asin", operands);
    }

    @Override
    protected Asin copy(List<Expression> newOperands) {
        return new Asin(newOperands);
    }
}
