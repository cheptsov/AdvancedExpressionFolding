package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Min extends Function implements ArithmeticExpression {
    public Min(TextRange textRange, List<Expression> operands) {
        super(textRange, "min", operands);
    }

    @Override
    protected Min copy(List<Expression> newOperands) {
        return new Min(textRange, newOperands);
    }
}
