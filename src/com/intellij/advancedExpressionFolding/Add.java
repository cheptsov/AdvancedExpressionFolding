package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Add extends Operation implements ArithmeticExpression {
    public Add(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expr> operands) {
        super(element, textRange, "+", 10, operands);
    }
}
