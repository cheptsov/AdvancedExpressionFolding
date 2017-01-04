package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class DivideAssign extends Operation implements ArithmeticExpression {
    public DivideAssign(TextRange textRange, List<Expression> operands) {
        super(textRange, "/=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new DivideAssign(textRange, newOperands);
    }
}
