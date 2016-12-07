package com.intellij.bigdecimal;

import java.util.List;

public class RemainderAssign extends Operation {
    public RemainderAssign(List<Expression> operands) {
        super("%=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new RemainderAssign(newOperands);
    }
}
