package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Not extends Function implements ArithmeticExpression {
    public Not(TextRange textRange, List<Expression> operands) {
        super(textRange, "negate", operands);
    }

    @Override
    protected Not copy(List<Expression> newOperands) {
        return new Not(textRange, newOperands);
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
