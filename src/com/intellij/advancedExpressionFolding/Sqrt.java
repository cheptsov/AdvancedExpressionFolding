package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Sqrt extends Function implements ArithmeticExpression {
    public Sqrt(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "sqrt", operands);
    }

    @Override
    protected Sqrt copy(List<Expression> newOperands) {
        return new Sqrt(element, textRange, newOperands);
    }

    @Override
    public String format() {
        if (operands.get(0) instanceof NumberLiteral ||
                operands.get(0) instanceof Variable) {
            return "√" + operands.get(0).format();
        } else {
            return "√" + "(" + operands.get(0).format() + ")";
        }
    }
}
