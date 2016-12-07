package com.intellij.bigdecimal;

import java.util.List;

public class ShiftRight extends Operation {
    public ShiftRight(List<Expression> operands) {
        super(">>", 20, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftRight(operands);
    }
}
