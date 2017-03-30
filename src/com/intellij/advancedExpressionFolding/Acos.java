package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Acos extends Function implements ArithmeticExpression {
    public Acos(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "acos", operands);
    }

    @Override
    protected Acos copy(List<Expression> newOperands) {
        return new Acos(element, textRange, newOperands);
    }
}
