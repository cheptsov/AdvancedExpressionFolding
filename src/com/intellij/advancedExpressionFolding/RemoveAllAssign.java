package com.intellij.advancedExpressionFolding;

import java.util.List;

public class RemoveAllAssign extends Operation {
    public RemoveAllAssign(List<Expression> operands) {
        super("--=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new RemoveAllAssign(newOperands);
    }
}
