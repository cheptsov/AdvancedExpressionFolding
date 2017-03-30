package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class AndAssign extends Operation implements ArithmeticExpression {
    public AndAssign(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "&=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new AndAssign(element, textRange, newOperands);
    }
}
