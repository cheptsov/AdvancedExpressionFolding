package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiCodeBlock;
import org.jetbrains.annotations.NotNull;

public class ControlFlowMultiStatementCodeBlockExpression extends AbstractControlFlowCodeBlock implements ControlFlowMultiStatementCodeBlock {
    public ControlFlowMultiStatementCodeBlockExpression(@NotNull PsiCodeBlock element,
                                                        @NotNull TextRange textRange) {
        super(element, textRange);
    }
}
