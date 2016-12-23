package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class ExpressionTextRange {
    private Expression expression;
    private TextRange textRange;

    public Expression getExpression() {
        return expression;
    }

    public TextRange getTextRange() {
        return textRange;
    }

    public ExpressionTextRange(Expression expression, TextRange textRange) {
        this.expression = expression;

        this.textRange = textRange;
    }
}
