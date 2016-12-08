package com.intellij.bigdecimal;

import java.util.List;

public class Sqrt extends Function {
    public Sqrt(List<Expression> operands) {
        super("sqrt", operands);
    }

    @Override
    protected Sqrt copy(List<Expression> newOperands) {
        return new Sqrt(newOperands);
    }

    @Override
    public String format() {
        if (operands.get(0) instanceof Literal ||
                operands.get(0) instanceof Variable) {
            return "√" + operands.get(0).format();
        } else {
            return "√" + "(" + operands.get(0).format() + ")";
        }
    }
}
