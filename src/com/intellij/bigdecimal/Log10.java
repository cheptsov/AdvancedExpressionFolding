package com.intellij.bigdecimal;

import java.util.List;

public class Log10 extends Function {
    public Log10(List<Expression> operands) {
        super("log", operands);
    }

    @Override
    protected Log10 copy(List<Expression> newOperands) {
        return new Log10(newOperands);
    }

    @Override
    public String format() {
        if (operands.get(0) instanceof NumberLiteral ||
                operands.get(0) instanceof Variable) {
            return "log" + subscript("10") + operands.get(0).format();
        } else {
            return "log" + subscript("10") + "(" + super.format() + ")";
        }
    }
}
