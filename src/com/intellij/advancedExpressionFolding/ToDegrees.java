package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class ToDegrees extends Function implements ArithmeticExpression {
    public ToDegrees(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "toDegrees", operands);
    }
}
