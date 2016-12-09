package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Round extends Function implements ArithmeticExpression {
    public Round(List<Expression> operands) {
        super("round", operands);
    }

    @Override
    protected Round copy(List<Expression> newOperands) {
        return new Round(newOperands);
    }
}
