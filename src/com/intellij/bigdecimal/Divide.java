package com.intellij.bigdecimal;

import java.util.List;

public class Divide extends Operation {
    public Divide(List<Expression> operands) {
        super("/", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Divide(newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
