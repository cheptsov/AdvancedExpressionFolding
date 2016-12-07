package com.intellij.bigdecimal;

import java.util.List;

public class GreaterEqual extends Operation {
    public GreaterEqual(List<Expression> operands) {
        super("≥", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new GreaterEqual(operands);
    }
}
