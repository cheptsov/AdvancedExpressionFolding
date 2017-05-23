package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableDeclarationImpl extends Expression implements VariableDeclaration {
    private final boolean isFinal;

    public VariableDeclarationImpl(@NotNull PsiElement element, @NotNull TextRange textRange, boolean isFinal) {
        super(element, textRange);
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(VariableDeclarationImpl.class.getName());
        return new FoldingDescriptor[] {
                new FoldingDescriptor(element.getNode(), textRange, group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return isFinal ? "val" : "var";
                    }
                }
        };
    }
}
