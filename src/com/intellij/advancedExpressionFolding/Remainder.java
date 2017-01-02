package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Remainder extends Operation implements ArithmeticExpression {
    public Remainder(TextRange textRange, List<Expression> operands) {
        super(textRange, "%", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Remainder(textRange, operands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
