package com.intellij.bigdecimal;

import java.util.List;

public class Max extends Function {
    public Max(List<Expression> operands) {
        super("max", operands);
    }

    @Override
    protected Max copy(List<Expression> newOperands) {
        return new Max(newOperands);
    }
}
