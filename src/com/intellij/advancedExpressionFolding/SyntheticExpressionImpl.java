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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyntheticExpressionImpl that = (SyntheticExpressionImpl) o;

        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
