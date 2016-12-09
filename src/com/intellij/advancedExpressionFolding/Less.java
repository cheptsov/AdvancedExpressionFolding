package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Less extends Operation implements ComparingExpression {
    public Less(List<Expression> operands) {
        super("<", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Less(operands);
    }
}
