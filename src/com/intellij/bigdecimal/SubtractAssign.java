package com.intellij.bigdecimal;

import java.util.List;

public class SubtractAssign extends Operation {
    public SubtractAssign(List<Expression> operands) {
        super("-=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new SubtractAssign(newOperands);
    }
}
