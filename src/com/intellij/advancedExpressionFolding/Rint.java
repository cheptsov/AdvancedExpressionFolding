package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Rint extends Function implements ArithmeticExpression {
    public Rint(TextRange textRange, List<Expression> operands) {
        super(textRange, "rint", operands);
    }

    @Override
    protected Rint copy(List<Expression> newOperands) {
        return new Rint(textRange, newOperands);
    }
}
