package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Cbrt extends Function implements ArithmeticExpression {
    public Cbrt(TextRange textRange, List<Expression> operands) {
        super(textRange, "cbrt", operands);
    }

    @Override
    protected Cbrt copy(List<Expression> newOperands) {
        return new Cbrt(textRange, newOperands);
    }

    @Override
    public String format() {
        if (operands.get(0) instanceof NumberLiteral ||
                operands.get(0) instanceof Variable) {
            return "∛" + operands.get(0).format();
        } else {
            return "∛" + "(" + operands.get(0).format() + ")";
        }
    }
}
