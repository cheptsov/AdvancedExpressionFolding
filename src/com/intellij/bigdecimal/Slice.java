package com.intellij.bigdecimal;

import java.util.List;

public class Slice extends Function {
    public Slice(List<Expression> operands) {
        super("slice", operands);
    }

    @Override
    protected Slice copy(List<Expression> newOperands) {
        return new Slice(newOperands);
    }

    @Override
    public String format() {
        String q = operands.get(0).format();
        String r1 = operands.get(1) instanceof NumberLiteral &&
                ((NumberLiteral) operands.get(1)).getNumber().intValue() < 1 ?
                "" : operands.get(1).format();
        String r2 = operands.get(2) instanceof NumberLiteral &&
                ((NumberLiteral) operands.get(2)).getNumber().equals(-1) ?
                "" : operands.get(2).format();
        return q + "[" + r1 + ":" + r2 + "]";
    }

}
