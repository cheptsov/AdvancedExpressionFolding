package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Log extends Function implements ArithmeticExpression {
    public Log(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "ln", operands);
    }
}
