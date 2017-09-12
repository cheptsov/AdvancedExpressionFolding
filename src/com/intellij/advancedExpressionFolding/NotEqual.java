package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class NotEqual extends Operation implements ComparingExpression {
    public NotEqual(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "≢", 18, operands);
    }
}
