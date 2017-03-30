package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Ceil extends Function implements ArithmeticExpression {
    public Ceil(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange,"ceil", operands);
    }

    @Override
    protected Ceil copy(List<Expression> newOperands) {
        return new Ceil(element, textRange, newOperands);
    }
}
