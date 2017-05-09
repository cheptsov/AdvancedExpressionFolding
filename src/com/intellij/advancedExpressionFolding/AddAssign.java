package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class AddAssign extends Operation implements ArithmeticExpression {
    public AddAssign(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "+=", 300, operands);
    }
}
