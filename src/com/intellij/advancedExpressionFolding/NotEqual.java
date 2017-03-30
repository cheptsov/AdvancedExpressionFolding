package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class NotEqual extends Operation implements ComparingExpression {
    public NotEqual(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "≢", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new NotEqual(element, textRange, operands);
    }
}
