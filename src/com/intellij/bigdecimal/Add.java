package com.intellij.bigdecimal;

import java.util.List;

public class Add extends Operation {
    public Add(List<Expression> operands) {
        super("+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Add(operands);
    }
}
