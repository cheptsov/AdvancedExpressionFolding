package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class AddAssignForCollection extends Operation implements ConcatenationExpression {
    public AddAssignForCollection(TextRange textRange, List<Expression> operands) {
        super(textRange, "+=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new AddAssignForCollection(textRange, newOperands);
    }
}
