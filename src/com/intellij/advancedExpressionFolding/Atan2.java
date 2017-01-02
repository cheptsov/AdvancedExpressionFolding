package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Atan2 extends Function implements ArithmeticExpression {
    public Atan2(TextRange textRange, List<Expression> operands) {
        super(textRange, "atan2", operands);
    }

    @Override
    protected Atan2 copy(List<Expression> newOperands) {
        return new Atan2(textRange, newOperands);
    }
}
