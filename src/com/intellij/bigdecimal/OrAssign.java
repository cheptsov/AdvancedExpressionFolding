package com.intellij.bigdecimal;

import java.util.List;

public class OrAssign extends Operation {
    public OrAssign(List<Expression> operands) {
        super("|=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new OrAssign(newOperands);
    }
}
