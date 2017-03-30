package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class Subtract extends Operation implements ArithmeticExpression {

    public Subtract(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "-", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Subtract(element, textRange, newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public Expression simplify(boolean compute) {
        if (compute) {
            Operation s = (Operation) super.simplify(true);
            if (s.operands.stream().allMatch(o -> o instanceof NumberLiteral)) {
                int n1 = ((NumberLiteral) s.operands.get(0)).getNumber().intValue();
                int na = s.operands.stream().skip(1).mapToInt(o -> ((NumberLiteral) o).getNumber().intValue()).sum();
                return new NumberLiteral(element, textRange, n1 - na);
            } else {
                return s;
            }
        } else {
            return super.simplify(false);
        }
    }
}
