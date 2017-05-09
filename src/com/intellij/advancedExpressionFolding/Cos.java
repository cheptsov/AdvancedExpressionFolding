package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Cos extends Function implements ArithmeticExpression {
    public Cos(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "cos", operands);
    }
}
