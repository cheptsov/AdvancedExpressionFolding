package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class ShiftLeftAssign extends Operation implements ArithmeticExpression {
    public ShiftLeftAssign(TextRange textRange, List<Expression> operands) {
        super(textRange, "<<=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftLeftAssign(textRange, newOperands);
    }
}
