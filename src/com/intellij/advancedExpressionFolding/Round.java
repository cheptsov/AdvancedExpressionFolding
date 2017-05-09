package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Round extends Function implements ArithmeticExpression {
    public Round(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "round", operands);
    }
}
