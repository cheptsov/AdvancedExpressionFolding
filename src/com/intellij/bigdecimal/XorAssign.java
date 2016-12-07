package com.intellij.bigdecimal;

import java.util.List;

public class XorAssign extends Operation {
    public XorAssign(List<Expression> operands) {
        super("^=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new XorAssign(newOperands);
    }
}
