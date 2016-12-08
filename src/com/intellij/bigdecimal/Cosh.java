package com.intellij.bigdecimal;

import java.util.List;

public class Cosh extends Function {
    public Cosh(List<Expression> operands) {
        super("cosh", operands);
    }

    @Override
    protected Cosh copy(List<Expression> newOperands) {
        return new Cosh(newOperands);
    }
}
