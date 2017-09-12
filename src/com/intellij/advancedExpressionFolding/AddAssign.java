package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddAssign extends Operation implements ArithmeticExpression {
    public AddAssign(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expr> operands) {
        super(element, textRange, "+=", 300, operands);
    }
}
