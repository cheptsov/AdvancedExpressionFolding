package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class XorAssign extends Operation implements ArithmeticExpression {
    public XorAssign(TextRange textRange, List<Expression> operands) {
        super(textRange, "^=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new XorAssign(textRange, newOperands);
    }
}
