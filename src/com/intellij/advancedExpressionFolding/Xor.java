package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Xor extends Operation implements ArithmeticExpression {
    public Xor(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "^", 50, operands);
    }

    @Override
    protected Xor copy(List<Expression> newOperands) {
        return new Xor(element, textRange, newOperands);
    }
}
