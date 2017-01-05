package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.*;
import java.util.stream.Collectors;

public class Multiply extends Operation implements ArithmeticExpression {
    public Multiply(TextRange textRange, List<Expression> operands) {
        super(textRange, "*", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Multiply(textRange, newOperands);
    }

    @Override
    public Expression simplify(boolean compute) {
        final boolean[] simplified = {false};
        List<Expression> operands = ((Operation) super.simplify(compute)).operands;
        if (!operands.equals(this.operands)) {
            simplified[0] = true;
        }
        Map<Expression, Integer> map = new LinkedHashMap<>();
        for (Expression operand : operands) {
            Expression o = operand.simplify();
            if (o != operand) {
                simplified[0] = true;
            }
            map.compute(operand, (s, counter) -> {
                if (counter != null) {
                    simplified[0] = true;
                    return counter + 1;
                } else {
                    return 1;
                }
            });
        }
        if (simplified[0]) {
            List<Expression> simplifiedOperands = map.entrySet().stream().map(e ->
                    e.getValue() == 1 ? e.getKey() :
                            new Pow(null, Arrays.asList(e.getKey(), new NumberLiteral(null, e.getValue())))).collect(Collectors.toList());
            if (simplified.length == 1 && simplifiedOperands.get(0) instanceof Pow && simplifiedOperands.get(0).getTextRange() == null) {
                return new Pow(getTextRange(), ((Pow) simplifiedOperands.get(0)).operands);
            } else {
                return new Multiply(textRange, simplifiedOperands);
            }
        } else {
            return this;
        }
    }

}
