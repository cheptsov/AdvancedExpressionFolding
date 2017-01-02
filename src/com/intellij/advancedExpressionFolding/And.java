package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class And extends Operation implements ArithmeticExpression {
    public And(TextRange textRange, List<Expression> operands) {
        super(textRange, "&", 50, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new And(textRange, operands);
    }
}
