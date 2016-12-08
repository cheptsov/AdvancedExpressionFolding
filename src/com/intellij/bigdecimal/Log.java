package com.intellij.bigdecimal;

import java.util.List;

public class Log extends Function {
    public Log(List<Expression> operands) {
        super("ln", operands);
    }

    @Override
    protected Log copy(List<Expression> newOperands) {
        return new Log(newOperands);
    }
}
