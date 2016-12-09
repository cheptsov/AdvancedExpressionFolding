package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Subtract extends Operation implements ArithmeticExpression {

    public Subtract(List<Expression> operands) {
        super("-", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Subtract(newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
