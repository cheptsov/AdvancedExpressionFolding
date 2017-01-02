package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Tanh extends Function implements ArithmeticExpression {
    public Tanh(TextRange textRange, List<Expression> operands) {
        super(textRange, "tanh", operands);
    }

    @Override
    protected Tanh copy(List<Expression> newOperands) {
        return new Tanh(textRange, newOperands);
    }
}
