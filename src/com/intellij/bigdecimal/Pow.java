package com.intellij.bigdecimal;

import java.util.List;

public class Pow extends Operation {
    public Pow(List<Expression> operands) {
        super("^", operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Pow(newOperands);
    }

    @Override
    protected int compareTo(Operation operation) {
        return operation instanceof Pow
                ? 0
                : 1;

    }

}
