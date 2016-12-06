package com.intellij.bigdecimal;

import java.util.List;

public class Abs extends Function {
    public Abs(List<Expression> operands) {
        super("abs", operands);
    }

    @Override
    protected Abs copy(List<Expression> newOperands) {
        return new Abs(newOperands);
    }

    @Override
    public String format() {
        return "|" + operands.get(0).format() + "|";
    }
}
