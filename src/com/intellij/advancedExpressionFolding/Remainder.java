package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Remainder extends Operation implements ArithmeticExpression {
    public Remainder(List<Expression> operands) {
        super("%", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Remainder(operands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
