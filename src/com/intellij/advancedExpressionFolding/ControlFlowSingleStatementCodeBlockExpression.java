package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiCodeBlock;
import org.jetbrains.annotations.NotNull;

public class ControlFlowSingleStatementCodeBlockExpression extends AbstractControlFlowCodeBlock {
    public ControlFlowSingleStatementCodeBlockExpression(@NotNull PsiCodeBlock element,
                                                         @NotNull TextRange textRange) {
        super(element, textRange);
    }
}
