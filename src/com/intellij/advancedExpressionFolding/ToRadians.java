package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class ToRadians extends Function implements ArithmeticExpression {
    public ToRadians(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "toRadians", operands);
    }
}
