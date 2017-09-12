package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class And extends Operation implements ArithmeticExpression {
    public And(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expr> operands) {
        super(element, textRange, "&", 50, operands);
    }
}
