package com.intellij.bigdecimal;

import java.util.List;

public class Add extends Operation {
    public Add(List<Expression> operands) {
        super("+", operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Add(operands);
    }

    @Override
    protected int compareTo(Operation operation) {
        return operation instanceof Add
                ? 0
                : -1;
    }
}
