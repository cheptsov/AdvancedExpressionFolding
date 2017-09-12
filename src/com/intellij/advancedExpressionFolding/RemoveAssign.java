package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class RemoveAssign extends Operation implements ArithmeticExpression {
    public RemoveAssign(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "-=", 300, operands);
    }
}
