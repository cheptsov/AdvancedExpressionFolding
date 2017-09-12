package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class MultiplyAssign extends Operation implements ArithmeticExpression {
    public MultiplyAssign(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "*=", 300, operands);
    }
}
