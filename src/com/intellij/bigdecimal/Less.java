package com.intellij.bigdecimal;

import java.util.List;

public class Less extends Operation {
    public Less(List<Expression> operands) {
        super("<", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Less(operands);
    }
}
