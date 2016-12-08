package com.intellij.bigdecimal;

import java.util.List;

public class Atan2 extends Function {
    public Atan2(List<Expression> operands) {
        super("atan2", operands);
    }

    @Override
    protected Atan2 copy(List<Expression> newOperands) {
        return new Atan2(newOperands);
    }
}
