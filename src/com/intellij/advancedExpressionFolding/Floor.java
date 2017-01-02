package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Floor extends Function implements ArithmeticExpression {
    public Floor(TextRange textRange, List<Expression> operands) {
        super(textRange, "floor", operands);
    }

    @Override
    protected Floor copy(List<Expression> newOperands) {
        return new Floor(textRange, newOperands);
    }
}
