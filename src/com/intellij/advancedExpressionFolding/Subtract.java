package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Subtract extends Operation implements ArithmeticExpression {

    public Subtract(List<Expression> operands) {
        super("-", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Subtract(newOperands);
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
                return new NumberLiteral(n1 - na);
            } else {
                return s;
            }
        } else {
            return super.simplify(false);
        }
    }
}
