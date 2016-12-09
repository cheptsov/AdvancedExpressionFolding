package com.intellij.advancedExpressionFolding;

import java.util.List;

public class ToRadians extends Function implements ArithmeticExpression {
    public ToRadians(List<Expression> operands) {
        super("toRadians", operands);
    }

    @Override
    protected ToRadians copy(List<Expression> newOperands) {
        return new ToRadians(newOperands);
    }
}
