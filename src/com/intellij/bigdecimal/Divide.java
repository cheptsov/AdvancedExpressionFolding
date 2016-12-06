package com.intellij.bigdecimal;

import java.util.List;

public class Divide extends Operation {
    public Divide(List<Expression> operands) {
        super("/", operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Divide(newOperands);
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

    @Override
    public boolean isAssociative() {
        return false;
    }
}
