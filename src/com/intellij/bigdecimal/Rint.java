package com.intellij.bigdecimal;

import java.util.List;

public class Rint extends Function {
    public Rint(List<Expression> operands) {
        super("rint", operands);
    }

    @Override
    protected Rint copy(List<Expression> newOperands) {
        return new Rint(newOperands);
    }
}
