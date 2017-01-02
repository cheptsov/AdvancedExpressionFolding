package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class SyntheticExpressionImpl extends Expression implements SyntheticExpression {
    private final String text;

    public SyntheticExpressionImpl(TextRange textRange, String text) {
        super(textRange);
        this.text = text;
    }

    @Override
    public String format() {
        return text;
    }
}
