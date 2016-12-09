package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Atan2 extends Function implements ArithmeticExpression {
    public Atan2(List<Expression> operands) {
        super("atan2", operands);
    }

    @Override
    protected Atan2 copy(List<Expression> newOperands) {
        return new Atan2(newOperands);
    }
}
