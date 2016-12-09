package com.intellij.advancedExpressionFolding;

import java.util.List;

public class AddAssign extends Operation implements ArithmeticExpression {
    public AddAssign(List<Expression> operands) {
        super("+=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new AddAssign(newOperands);
    }
}
