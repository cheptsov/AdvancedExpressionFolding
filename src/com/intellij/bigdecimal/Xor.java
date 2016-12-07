package com.intellij.bigdecimal;

import java.util.List;

public class Xor extends Operation {
    public Xor(List<Expression> operands) {
        super("^", 50, operands);
    }

    @Override
    protected Xor copy(List<Expression> newOperands) {
        return new Xor(newOperands);
    }
}
