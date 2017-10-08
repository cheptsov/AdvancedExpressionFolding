package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public class RemoveAssignForCollection extends Operation {
    public RemoveAssignForCollection(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "-=", 300, operands);
    }
}
