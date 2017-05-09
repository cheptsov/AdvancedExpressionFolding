package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Not extends Function implements ArithmeticExpression {
    public Not(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "negate", operands);
    }
}
