package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Ceil extends Function implements ArithmeticExpression {
    public Ceil(TextRange textRange, List<Expression> operands) {
        super(textRange,"ceil", operands);
    }

    @Override
    protected Ceil copy(List<Expression> newOperands) {
        return new Ceil(textRange, newOperands);
    }
}
