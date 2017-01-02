package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Or extends Operation implements ArithmeticExpression {
    public Or(TextRange textRange, List<Expression> operands) {
        super(textRange, "|", 49, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Or(textRange, operands);
    }
}
