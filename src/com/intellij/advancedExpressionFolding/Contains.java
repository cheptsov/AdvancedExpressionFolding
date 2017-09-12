package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class Contains extends Expr implements CheckExpression {
    private final @NotNull
    Expr object;
    private final @NotNull
    Expr key;

    public Contains(PsiElement element, TextRange textRange, @NotNull Expr object, @NotNull Expr key) {
        super(element, textRange);
        this.object = object;
        this.key = key;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expr parent) {
        FoldingGroup group = FoldingGroup.newGroup(Contains.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(object.getTextRange().getEndOffset(),
                        key.getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "[";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(key.getTextRange().getEndOffset(),
                        getTextRange().getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "]?";
            }
        });
        if (object.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document, this));
        }
        if (key.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, key.buildFoldRegions(key.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
