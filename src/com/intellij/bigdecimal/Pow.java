package com.intellij.bigdecimal;

import java.util.List;

public class Pow extends Operation {
    public Pow(List<Expression> operands) {
        super("^", 200, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Pow(newOperands);
    }
}
