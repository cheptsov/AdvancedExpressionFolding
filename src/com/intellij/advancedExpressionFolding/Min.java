package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Min extends Function implements ArithmeticExpression {
    public Min(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "min", operands);
    }
}
