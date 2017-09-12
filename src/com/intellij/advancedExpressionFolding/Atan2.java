package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Atan2 extends Function implements ArithmeticExpression {
    public Atan2(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "atan2", operands);
    }
}
