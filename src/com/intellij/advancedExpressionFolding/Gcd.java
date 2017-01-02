package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Gcd extends Function implements ArithmeticExpression {
    public Gcd(TextRange textRange, List<Expression> operands) {
        super(textRange, "gcd", operands);
    }

    @Override
    protected Gcd copy(List<Expression> newOperands) {
        return new Gcd(textRange, newOperands);
    }
}
