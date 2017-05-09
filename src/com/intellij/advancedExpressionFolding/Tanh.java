package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Tanh extends Function implements ArithmeticExpression {
    public Tanh(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "tanh", operands);
    }
}
