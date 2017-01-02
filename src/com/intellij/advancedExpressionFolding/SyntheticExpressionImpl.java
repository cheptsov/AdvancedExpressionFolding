package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class SyntheticExpressionImpl extends Expression implements SyntheticExpression {
    public SyntheticExpressionImpl(TextRange textRange) {
        super(textRange);
    }

    @Override
    public String format() {
        throw new UnsupportedOperationException();
    }
}
