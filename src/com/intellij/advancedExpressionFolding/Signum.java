package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Signum extends Function implements ArithmeticExpression {
    public Signum(TextRange textRange, List<Expression> operands) {
        super(textRange, "signum", operands);
    }

    @Override
    protected Signum copy(List<Expression> newOperands) {
        return new Signum(textRange, newOperands);
    }
}
