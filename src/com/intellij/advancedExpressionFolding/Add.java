package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Add extends Operation implements ArithmeticExpression {
    public Add(TextRange textRange, List<Expression> operands) {
        super(textRange, "+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Add(textRange, operands);
    }
}
