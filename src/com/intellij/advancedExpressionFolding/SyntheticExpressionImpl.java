package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class SyntheticExpressionImpl extends Expression {
    private final String text;
    private final ArrayList<Expression> children;

    public SyntheticExpressionImpl(PsiElement element, TextRange textRange, String text,
                                   ArrayList<Expression> children) {
        super(element, textRange);
        this.text = text;
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyntheticExpressionImpl that = (SyntheticExpressionImpl) o;

        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return children.size() > 0 && children.stream().anyMatch(e -> e.supportsFoldRegions(document, this));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        for (Expression child : children) {
            if (child.supportsFoldRegions(document, this)) {
                Collections.addAll(descriptors, child.buildFoldRegions(child.getElement(), document, this));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
