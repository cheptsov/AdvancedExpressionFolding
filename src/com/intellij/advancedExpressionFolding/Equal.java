package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Equal extends Operation implements ComparingExpression {
    public Equal(List<Expression> operands) {
        super("≡", 18, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Equal(operands);
    }
}
