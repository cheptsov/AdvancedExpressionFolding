package com.intellij.bigdecimal;

import java.util.List;

public class Or extends Operation {
    public Or(List<Expression> operands) {
        super("|", 49, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Or(operands);
    }
}
