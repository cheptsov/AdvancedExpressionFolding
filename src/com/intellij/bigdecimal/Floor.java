package com.intellij.bigdecimal;

import java.util.List;

public class Floor extends Function {
    public Floor(List<Expression> operands) {
        super("floor", operands);
    }

    @Override
    protected Floor copy(List<Expression> newOperands) {
        return new Floor(newOperands);
    }
}
