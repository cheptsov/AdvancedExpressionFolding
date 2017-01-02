package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Cos extends Function implements ArithmeticExpression {
    public Cos(TextRange textRange, List<Expression> operands) {
        super(textRange, "cos", operands);
    }

    @Override
    protected Cos copy(List<Expression> newOperands) {
        return new Cos(textRange, newOperands);
    }
}
