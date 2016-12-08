package com.intellij.bigdecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Function extends Expression {
    private final String name;
    protected final List<Expression> operands;

    public Function(String name, List<Expression> operands) {
        this.name = name;
        this.operands = operands;
    }

    @Override
    public String format() {
        return name + "(" + operands.stream().map(Expression::format).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public Function simplify() {
        List<Expression> simplifiedOperands = null;
        for (int i = 0; i < operands.size(); i++) {
            Expression operand = operands.get(i);
            Expression simplifiedOperand = operand.simplify();
            if (simplifiedOperand != operand) {
                if (simplifiedOperands == null) {
                    simplifiedOperands = new ArrayList<>();
                    if (i > 0) {
                        simplifiedOperands.addAll(operands.subList(0, i));
                    }
                }
                simplifiedOperands.add(simplifiedOperand);
            } else {
                if (simplifiedOperands != null) {
                    simplifiedOperands.add(operand);
                }
            }
        }
        return simplifiedOperands != null ? (Function) copy(simplifiedOperands) : this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Function function = (Function) o;

        if (!name.equals(function.name)) return false;
        return operands.equals(function.operands);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operands.hashCode();
        return result;
    }
}
