package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class ForStatement extends Range {
    public static final String FOR_SEPARATOR = ":";

    public ForStatement(TextRange textRange,
                        Expression operand, Expression startRange, boolean startInclusive,
                        Expression endRange, boolean endInclusive) {
        super(textRange, operand, startRange, startInclusive, endRange, endInclusive);
        this.separator = FOR_SEPARATOR;
    }
}
