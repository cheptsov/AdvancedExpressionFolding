package com.intellij.bigdecimal;

import java.util.List;

public class Acos extends Function {
    public Acos(List<Expression> operands) {
        super("acos", operands);
    }

    @Override
    protected Acos copy(List<Expression> newOperands) {
        return new Acos(newOperands);
    }
}
