package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class ToRadians extends Function implements ArithmeticExpression {
    public ToRadians(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "toRadians", operands);
    }

    @Override
    protected ToRadians copy(List<Expression> newOperands) {
        return new ToRadians(element, textRange, newOperands);
    }
}
