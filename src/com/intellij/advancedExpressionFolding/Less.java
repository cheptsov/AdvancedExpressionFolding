package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Less extends Operation implements ComparingExpression {
    public Less(TextRange textRange, List<Expression> operands) {
        super(textRange, "<", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Less(textRange, operands);
    }
}
