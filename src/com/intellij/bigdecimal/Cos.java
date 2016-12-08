package com.intellij.bigdecimal;

import java.util.List;

public class Cos extends Function {
    public Cos(List<Expression> operands) {
        super("cos", operands);
    }

    @Override
    protected Cos copy(List<Expression> newOperands) {
        return new Cos(newOperands);
    }
}
