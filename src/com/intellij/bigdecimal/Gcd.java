package com.intellij.bigdecimal;

import java.util.List;

public class Gcd extends Function {
    public Gcd(List<Expression> operands) {
        super("gcd", operands);
    }

    @Override
    protected Gcd copy(List<Expression> newOperands) {
        return new Gcd(newOperands);
    }
}
