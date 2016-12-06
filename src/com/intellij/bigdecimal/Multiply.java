package com.intellij.bigdecimal;

import java.util.List;

public class Multiply extends Operation {
    public Multiply(List<Expression> operands) {
        super("*", operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Multiply(newOperands);
    }

    @Override
    protected int compareTo(Operation operation) {
        return operation instanceof Pow
                ? -1
                : operation instanceof Multiply
                ? 0
                : operation instanceof Divide
                ? 0
                : 1;

    }

}
