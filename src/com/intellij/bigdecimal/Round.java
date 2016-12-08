package com.intellij.bigdecimal;

import java.util.List;

public class Round extends Function {
    public Round(List<Expression> operands) {
        super("round", operands);
    }

    @Override
    protected Round copy(List<Expression> newOperands) {
        return new Round(newOperands);
    }
}
