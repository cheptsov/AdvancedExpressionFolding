package com.intellij.advancedExpressionFolding;

import java.util.List;

public class LessEqual extends Operation implements ComparingExpression {
    public LessEqual(List<Expression> operands) {
        super("≤", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new LessEqual(operands);
    }
}
