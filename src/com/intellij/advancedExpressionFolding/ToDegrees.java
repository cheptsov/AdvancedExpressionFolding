package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class ToDegrees extends Function implements ArithmeticExpression {
    public ToDegrees(TextRange textRange, List<Expression> operands) {
        super(textRange, "toDegrees", operands);
    }

    @Override
    protected ToDegrees copy(List<Expression> newOperands) {
        return new ToDegrees(textRange, newOperands);
    }
}
