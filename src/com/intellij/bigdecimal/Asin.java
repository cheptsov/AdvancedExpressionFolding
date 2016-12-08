package com.intellij.bigdecimal;

import java.util.List;

public class Asin extends Function {
    public Asin(List<Expression> operands) {
        super("asin", operands);
    }

    @Override
    protected Asin copy(List<Expression> newOperands) {
        return new Asin(newOperands);
    }
}
