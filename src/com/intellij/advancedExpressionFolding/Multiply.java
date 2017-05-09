package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Multiply extends Operation implements ArithmeticExpression {
    public Multiply(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "*", 100, operands);
    }
}
