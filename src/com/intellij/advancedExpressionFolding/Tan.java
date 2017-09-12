package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Tan extends Function implements ArithmeticExpression {
    public Tan(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "tan", operands);
    }
}
