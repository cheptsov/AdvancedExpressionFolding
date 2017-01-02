package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class NotEqual extends Operation implements ArithmeticExpression {
    public NotEqual(TextRange textRange, List<Expression> operands) {
        super(textRange, "≢", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new NotEqual(textRange, operands);
    }
}
