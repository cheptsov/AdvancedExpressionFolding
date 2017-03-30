package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Add extends Operation implements ArithmeticExpression {
    public Add(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Add(element, textRange, operands);
    }
}
