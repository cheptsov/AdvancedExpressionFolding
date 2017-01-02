package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Pow extends Operation implements ArithmeticExpression {
    public Pow(TextRange textRange, List<Expression> operands) {
        super(textRange, "**", 200, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Pow(textRange, newOperands);
    }

    @Override
    public String format() {
        String a = operands.get(0).format();
        String b = operands.get(1).format();
        String bs = superscript(b);
        if (bs != null && !bs.contains(getCharacter())) {
            return a + bs;
        } else {
            return a + " " + getCharacter() + " " + b;
        }
    }
}
