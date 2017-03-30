package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Tan extends Function implements ArithmeticExpression {
    public Tan(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "tan", operands);
    }

    @Override
    protected Tan copy(List<Expression> newOperands) {
        return new Tan(element, textRange, newOperands);
    }
}
