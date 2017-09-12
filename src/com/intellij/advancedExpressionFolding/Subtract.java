package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Subtract extends Operation implements ArithmeticExpression {
    public Subtract(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "-", 10, operands);
    }
}
