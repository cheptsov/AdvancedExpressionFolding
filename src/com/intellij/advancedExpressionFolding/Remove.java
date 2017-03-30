package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Remove extends Operation {

    public Remove(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "-=", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Remove(element, textRange, newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
