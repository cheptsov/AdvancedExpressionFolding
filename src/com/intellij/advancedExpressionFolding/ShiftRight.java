package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class ShiftRight extends Operation implements ArithmeticExpression {
    public ShiftRight(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, ">>", 20, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftRight(element, textRange, operands);
    }
}
