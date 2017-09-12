package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Negate extends Function implements ArithmeticExpression {
    public Negate(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "negate", operands);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return false;
    }
}
