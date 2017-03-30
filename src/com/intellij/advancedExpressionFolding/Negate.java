package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Negate extends Function implements ArithmeticExpression {
    public Negate(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "negate", operands);
    }

    @Override
    protected Negate copy(List<Expression> newOperands) {
        return new Negate(element, textRange, newOperands);
    }

    @Override
    public String format() {
        Expression expression = operands.get(0);
        if (expression instanceof Operation) {
            return "-(" + expression.format() + ")";
        } else {
            return "-" + expression.format();
        }
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return false;
    }
}
