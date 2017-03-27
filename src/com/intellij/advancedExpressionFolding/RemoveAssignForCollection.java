package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class RemoveAssignForCollection extends Operation implements ConcatenationExpression {
    public RemoveAssignForCollection(TextRange textRange, List<Expression> operands) {
        super(textRange, "-=", 300, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new RemoveAssignForCollection(textRange, newOperands);
    }
}
