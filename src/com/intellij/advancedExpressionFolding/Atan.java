package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Atan extends Function implements ArithmeticExpression {
    public Atan(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "atan", operands);
    }

    @Override
    protected Atan copy(List<Expression> newOperands) {
        return new Atan(element, textRange, newOperands);
    }
}
