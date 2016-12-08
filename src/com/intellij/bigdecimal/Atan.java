package com.intellij.bigdecimal;

import java.util.List;

public class Atan extends Function {
    public Atan(List<Expression> operands) {
        super("atan", operands);
    }

    @Override
    protected Atan copy(List<Expression> newOperands) {
        return new Atan(newOperands);
    }
}
