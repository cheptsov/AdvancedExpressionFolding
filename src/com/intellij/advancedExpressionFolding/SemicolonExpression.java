package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SemicolonExpression extends Expression {
    public SemicolonExpression(PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expression parent) {
        return new FoldingDescriptor[] {
                new FoldingDescriptor(element.getNode(), textRange, FoldingGroup.newGroup(SemicolonExpression.class.getName())) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                }
        };
    }
}
