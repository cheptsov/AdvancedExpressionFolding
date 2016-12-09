package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Not extends Function implements ArithmeticExpression {
    public Not(List<Expression> operands) {
        super("negate", operands);
    }

    @Override
    protected Not copy(List<Expression> newOperands) {
        return new Not(newOperands);
    }

    @Override
    public String format() {
        Expression expression = operands.get(0);
        if (expression instanceof Operation) {
            return "~(" + expression.format() + ")";
        } else {
            return "~" + expression.format();
        }
    }
}
