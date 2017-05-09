package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Gcd extends Function implements ArithmeticExpression {
    public Gcd(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "gcd", operands);
    }
}
