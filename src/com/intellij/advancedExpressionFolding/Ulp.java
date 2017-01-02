package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Ulp extends Function implements ArithmeticExpression {
    public Ulp(TextRange textRange, List<Expression> operands) {
        super(textRange, "ulp", operands);
    }

    @Override
    protected Ulp copy(List<Expression> newOperands) {
        return new Ulp(textRange, newOperands);
    }
}
