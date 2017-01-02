package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class DivideAssign extends Operation implements ArithmeticExpression {
    private final boolean isCollapsedByDefault;

    public DivideAssign(TextRange textRange, List<Expression> operands, boolean isCollapsedByDefault) {
        super(textRange, "/=", 300, operands);
        this.isCollapsedByDefault = isCollapsedByDefault;
    }

    @Override
    public boolean isCollapsedByDefault() {
        return isCollapsedByDefault;
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new DivideAssign(textRange, newOperands, isCollapsedByDefault);
    }
}
