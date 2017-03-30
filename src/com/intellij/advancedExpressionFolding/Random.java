package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Random extends Function implements ArithmeticExpression {
    public Random(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "random", operands);
    }

    @Override
    protected Random copy(List<Expression> newOperands) {
        return new Random(element, textRange, newOperands);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
