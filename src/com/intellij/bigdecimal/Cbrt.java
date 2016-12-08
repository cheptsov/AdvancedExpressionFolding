package com.intellij.bigdecimal;

import java.util.List;

public class Cbrt extends Function {
    public Cbrt(List<Expression> operands) {
        super("cbrt", operands);
    }

    @Override
    protected Cbrt copy(List<Expression> newOperands) {
        return new Cbrt(newOperands);
    }

    @Override
    public String format() {
        if (operands.get(0) instanceof Literal ||
                operands.get(0) instanceof Variable) {
            return "∛" + operands.get(0).format();
        } else {
            return "∛" + "(" + operands.get(0).format() + ")";
        }
    }
}
