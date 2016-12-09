package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Greater extends Operation implements ComparingExpression {
    public Greater(List<Expression> operands) {
        super(">", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Greater(operands);
    }
}
