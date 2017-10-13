package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Equal extends Operation {
    public Equal(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "≡", 18, operands);
    }
}
