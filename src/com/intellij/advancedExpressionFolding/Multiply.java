package com.intellij.advancedExpressionFolding;

import java.util.*;
import java.util.stream.Collectors;

public class Multiply extends Operation implements ArithmeticExpression {
    public Multiply(List<Expression> operands) {
        super("*", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Multiply(newOperands);
    }

    @Override
    public Operation simplify() {
        final boolean[] simplified = {false};
        List<Expression> operands = super.simplify().operands;
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
                    e.getValue() == 1 ? e.getKey() : new Pow(Arrays.asList(e.getKey(), new NumberLiteral(e.getValue())))
            ).collect(Collectors.toList());
            return new Multiply(simplifiedOperands);
        } else {
            return this;
        }
    }

}
