package com.intellij.bigdecimal;

import java.util.List;

public class ShiftLeft extends Operation {
    public ShiftLeft(List<Expression> operands) {
        super("<<", 20, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftLeft(operands);
    }
}
