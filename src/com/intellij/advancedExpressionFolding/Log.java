package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Log extends Function implements ArithmeticExpression {
    public Log(TextRange textRange, List<Expression> operands) {
        super(textRange, "ln", operands);
    }

    @Override
    protected Log copy(List<Expression> newOperands) {
        return new Log(textRange, newOperands);
    }
}
