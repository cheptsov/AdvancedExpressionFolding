package com.intellij.bigdecimal;

import java.util.List;

public class Tanh extends Function {
    public Tanh(List<Expression> operands) {
        super("tanh", operands);
    }

    @Override
    protected Tanh copy(List<Expression> newOperands) {
        return new Tanh(newOperands);
    }
}
