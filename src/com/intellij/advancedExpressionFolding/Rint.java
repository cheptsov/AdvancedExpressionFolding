package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Rint extends Function implements ArithmeticExpression {
    public Rint(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "rint", operands);
    }
}
