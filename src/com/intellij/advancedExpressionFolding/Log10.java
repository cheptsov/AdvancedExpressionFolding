package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Log10 extends Function implements ArithmeticExpression {
    public Log10(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "log", operands);
    }

    @Override
    protected Log10 copy(List<Expression> newOperands) {
        return new Log10(element, textRange, newOperands);
    }

    @Override
    public String format() {
        if (operands.get(0) instanceof NumberLiteral ||
                operands.get(0) instanceof Variable) {
            return "log" + subscript("10") + operands.get(0).format();
        } else {
            return "log" + subscript("10") + "(" + super.format() + ")";
        }
    }
}
