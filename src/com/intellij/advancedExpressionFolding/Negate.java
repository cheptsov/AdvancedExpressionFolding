package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Negate extends Function implements ArithmeticExpression {
    public Negate(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "negate", operands);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return false;
    }
}
