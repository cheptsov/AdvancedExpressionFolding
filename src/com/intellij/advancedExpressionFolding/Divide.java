package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Divide extends Operation implements ArithmeticExpression {
    public Divide(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "/", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Divide(element, textRange, newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public String format() {
        if (operands.size() == 2 && operands.get(0) instanceof NumberLiteral && operands.get(1) instanceof NumberLiteral) {
            String format = format(((NumberLiteral) operands.get(0)).getNumber().doubleValue() / ((NumberLiteral) operands.get(1)).getNumber().doubleValue());
            if (format != null) {
                return format;
            }
        }
        return super.format();
    }
}
