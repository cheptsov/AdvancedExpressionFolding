package com.intellij.bigdecimal;

import java.util.List;

public class Subtract extends Operation {

    public Subtract(List<Expression> operands) {
        super("-", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Subtract(newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
