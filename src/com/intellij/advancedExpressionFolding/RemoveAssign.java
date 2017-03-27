package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class RemoveAssign extends Operation implements ArithmeticExpression {
    public RemoveAssign(TextRange textRange, List<Expression> operands) {
        super(textRange, "-=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new RemoveAssign(textRange, newOperands);
    }
}
