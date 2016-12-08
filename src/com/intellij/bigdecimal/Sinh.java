package com.intellij.bigdecimal;

import java.util.List;

public class Sinh extends Function {
    public Sinh(List<Expression> operands) {
        super("sinh", operands);
    }

    @Override
    protected Sinh copy(List<Expression> newOperands) {
        return new Sinh(newOperands);
    }
}
