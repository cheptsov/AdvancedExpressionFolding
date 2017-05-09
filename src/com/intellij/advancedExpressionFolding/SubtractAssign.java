package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class SubtractAssign extends Operation implements ArithmeticExpression {
    public SubtractAssign(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "-=", 300, operands);
    }
}
