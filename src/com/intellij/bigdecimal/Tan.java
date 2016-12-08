package com.intellij.bigdecimal;

import java.util.List;

public class Tan extends Function {
    public Tan(List<Expression> operands) {
        super("tan", operands);
    }

    @Override
    protected Tan copy(List<Expression> newOperands) {
        return new Tan(newOperands);
    }
}
