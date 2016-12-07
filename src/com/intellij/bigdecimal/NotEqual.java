package com.intellij.bigdecimal;

import java.util.List;

public class NotEqual extends Operation {
    public NotEqual(List<Expression> operands) {
        super("≢", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new NotEqual(operands);
    }
}
