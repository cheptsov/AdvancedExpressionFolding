package com.intellij.bigdecimal;

import java.util.List;

public class Negate extends Function {
    public Negate(List<Expression> operands) {
        super("negate", operands);
    }

    @Override
    protected Negate copy(List<Expression> newOperands) {
        return new Negate(newOperands);
    }

    @Override
    public String format() {
        return "- " + operands.get(0).format();
    }
}
