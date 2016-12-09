package com.intellij.advancedExpressionFolding;

import java.util.List;

public class XorAssign extends Operation implements ArithmeticExpression {
    public XorAssign(List<Expression> operands) {
        super("^=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new XorAssign(newOperands);
    }
}
