package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Sqrt extends Function implements ArithmeticExpression {
    public Sqrt(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "sqrt", operands);
    }
}
