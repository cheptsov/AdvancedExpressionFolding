package com.intellij.bigdecimal;

import java.util.List;

public class ShiftLeftAssign extends Operation {
    public ShiftLeftAssign(List<Expression> operands) {
        super("<<=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new ShiftLeftAssign(newOperands);
    }
}
