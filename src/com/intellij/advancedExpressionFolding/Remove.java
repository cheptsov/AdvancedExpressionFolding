package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Remove extends Operation {

    public Remove(List<Expression> operands) {
        super("-=", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Remove(newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
