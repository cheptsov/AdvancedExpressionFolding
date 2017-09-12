package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Max extends Function implements ArithmeticExpression {
    public Max(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "max", operands);
    }
}
