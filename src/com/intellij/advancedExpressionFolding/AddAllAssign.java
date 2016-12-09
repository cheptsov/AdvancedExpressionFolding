package com.intellij.advancedExpressionFolding;

import java.util.List;

public class AddAllAssign extends Operation {
    public AddAllAssign(List<Expression> operands) {
        super("++=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new AddAllAssign(newOperands);
    }
}
