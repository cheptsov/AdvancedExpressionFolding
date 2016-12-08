package com.intellij.bigdecimal;

import java.util.List;

public class Ceil extends Function {
    public Ceil(List<Expression> operands) {
        super("ceil", operands);
    }

    @Override
    protected Ceil copy(List<Expression> newOperands) {
        return new Ceil(newOperands);
    }
}
