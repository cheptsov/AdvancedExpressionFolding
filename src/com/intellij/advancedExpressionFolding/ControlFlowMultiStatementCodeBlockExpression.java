package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiCodeBlock;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class ControlFlowMultiStatementCodeBlockExpression extends AbstractControlFlowCodeBlock {
    public ControlFlowMultiStatementCodeBlockExpression(@NotNull PsiCodeBlock element,
                                                        @NotNull TextRange textRange) {
        super(element, textRange);
    }
}
