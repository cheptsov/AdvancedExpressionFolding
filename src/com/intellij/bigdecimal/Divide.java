package com.intellij.bigdecimal;

import java.util.List;

public class Divide extends Operation {
    public Divide(List<Expression> operands) {
        super("/", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Divide(newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public String format() {
        if (operands.size() == 2 && operands.get(0) instanceof Literal && operands.get(1) instanceof Literal) {
            String format = format(((Literal) operands.get(0)).getNumber().doubleValue() / ((Literal) operands.get(1)).getNumber().doubleValue());
            if (format != null) {
                return format;
            }
        }
        return super.format();
    }
}
