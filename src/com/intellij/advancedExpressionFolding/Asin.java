package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Asin extends Function implements ArithmeticExpression {
    public Asin(TextRange textRange, List<Expression> operands) {
        super(textRange, "asin", operands);
    }

    @Override
    protected Asin copy(List<Expression> newOperands) {
        return new Asin(textRange, newOperands);
    }
}
