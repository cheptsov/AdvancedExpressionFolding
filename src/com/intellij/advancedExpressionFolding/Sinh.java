package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Sinh extends Function implements ArithmeticExpression {
    public Sinh(TextRange textRange, List<Expression> operands) {
        super(textRange, "sinh", operands);
    }

    @Override
    protected Sinh copy(List<Expression> newOperands) {
        return new Sinh(textRange, newOperands);
    }
}
