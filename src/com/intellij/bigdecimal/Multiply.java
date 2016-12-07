package com.intellij.bigdecimal;

import java.util.List;

public class Multiply extends Operation {
    public Multiply(List<Expression> operands) {
        super("*", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Multiply(newOperands);
    }
}
