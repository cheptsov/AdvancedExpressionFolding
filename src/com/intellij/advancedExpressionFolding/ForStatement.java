package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

public class ForStatement extends Range {
    public static final String FOR_SEPARATOR = ":";

    public ForStatement(PsiElement element, TextRange textRange,
                        Expression operand, Expression startRange, boolean startInclusive,
                        Expression endRange, boolean endInclusive) {
        super(element, textRange, operand, startRange, startInclusive, endRange, endInclusive);
        this.separator = FOR_SEPARATOR;
    }
}
