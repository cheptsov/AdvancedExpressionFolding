package com.intellij.bigdecimal;

import java.util.List;

public class Subtract extends Operation {

    public Subtract(List<Expression> operands) {
        super("-", operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Subtract(newOperands);
    }

    @Override
    protected int compareTo(Operation operation) {
        return operation instanceof Subtract
                ? 0
                : operation instanceof Add
                ? 0
                : -1;
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
