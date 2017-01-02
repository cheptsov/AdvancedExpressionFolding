package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Acos extends Function implements ArithmeticExpression {
    public Acos(TextRange textRange, List<Expression> operands) {
        super(textRange, "acos", operands);
    }

    @Override
    protected Acos copy(List<Expression> newOperands) {
        return new Acos(textRange, newOperands);
    }
}
