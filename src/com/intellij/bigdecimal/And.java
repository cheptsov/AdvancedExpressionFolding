package com.intellij.bigdecimal;

import java.util.List;

public class And extends Operation {
    public And(List<Expression> operands) {
        super("&", 50, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new And(operands);
    }
}
