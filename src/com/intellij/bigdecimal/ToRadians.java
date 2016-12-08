package com.intellij.bigdecimal;

import java.util.List;

public class ToRadians extends Function {
    public ToRadians(List<Expression> operands) {
        super("toRadians", operands);
    }

    @Override
    protected ToRadians copy(List<Expression> newOperands) {
        return new ToRadians(newOperands);
    }
}
