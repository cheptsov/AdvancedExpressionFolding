package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SemicolonExpr extends Expr implements Semicolon {
    public SemicolonExpr(PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expr parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expr parent) {
        return new FoldingDescriptor[] {
                new FoldingDescriptor(element.getNode(), textRange, FoldingGroup.newGroup(SemicolonExpr.class.getName())) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                }
        };
    }
}
