package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Max extends Function implements ArithmeticExpression {
    public Max(TextRange textRange, List<Expression> operands) {
        super(textRange, "max", operands);
    }

    @Override
    protected Max copy(List<Expression> newOperands) {
        return new Max(textRange, newOperands);
    }
}
