package com.intellij.bigdecimal;

import java.util.List;

public class Reminder extends Operation {
    public Reminder(List<Expression> operands) {
        super("%", operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Reminder(operands);
    }

    @Override
    protected int compareTo(Operation operation) {
        return operation instanceof Reminder
                ? 0 :
                operation instanceof Add
                ? 1
                : operation instanceof Subtract
                ? 1
                : -1;
    }
}
