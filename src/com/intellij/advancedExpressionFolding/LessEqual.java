package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class LessEqual extends Operation implements ComparingExpression {
    public LessEqual(TextRange textRange, List<Expression> operands) {
        super(textRange, "≤", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new LessEqual(textRange, operands);
    }
}
