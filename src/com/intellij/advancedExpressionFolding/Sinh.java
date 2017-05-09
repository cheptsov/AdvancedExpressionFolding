package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Sinh extends Function implements ArithmeticExpression {
    public Sinh(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "sinh", operands);
    }
}
