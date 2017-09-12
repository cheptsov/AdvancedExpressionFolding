package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Log10 extends Function implements ArithmeticExpression {
    public Log10(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "log", operands);
    }
}
