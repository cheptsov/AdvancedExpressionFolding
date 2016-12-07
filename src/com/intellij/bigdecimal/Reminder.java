package com.intellij.bigdecimal;

import java.util.List;

public class Reminder extends Operation {
    public Reminder(List<Expression> operands) {
        super("%", 100, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Reminder(operands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
