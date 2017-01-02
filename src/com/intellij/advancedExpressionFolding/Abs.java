package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class Abs extends Function implements ArithmeticExpression {
    public Abs(TextRange textRange, List<Expression> operands) {
        super(textRange, "abs", operands);
    }

    @Override
    protected Abs copy(List<Expression> newOperands) {
        return new Abs(textRange, newOperands);
    }

    @Override
    public String format() {
        return "|" + operands.get(0).format() + "|";
    }
}
