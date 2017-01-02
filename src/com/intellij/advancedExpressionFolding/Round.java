package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Round extends Function implements ArithmeticExpression {
    public Round(TextRange textRange, List<Expression> operands) {
        super(textRange, "round", operands);
    }

    @Override
    protected Round copy(List<Expression> newOperands) {
        return new Round(textRange, newOperands);
    }
}
