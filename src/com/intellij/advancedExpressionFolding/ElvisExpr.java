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
import java.util.List;

public class ElvisExpr extends Expr implements CheckExpression {
    private final @NotNull
    Expr thenExpr;
    private final @NotNull
    Expr elseExpr;
    private final @NotNull List<TextRange> elements;

    public ElvisExpr(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expr thenExpr,
                     @NotNull Expr elseExpr,
                     @NotNull List<TextRange> elements) {
        super(element, textRange);
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
        this.elements = elements;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expr parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ElvisExpr.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(), thenExpr.getTextRange().getStartOffset()),
                group) {
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(thenExpr.getTextRange().getEndOffset(),
                        elseExpr.getTextRange().getStartOffset()),
                group) {
            @Override
            public String getPlaceholderText() {
                return " ?: "; // TODO: Eat spaces around
            }
        });
        ShortElvisExpr.nullify(element, document, descriptors, group, elements,
                !(elements.size() == 1 && elements.get(0).equals(thenExpr.getTextRange())));
        if (thenExpr.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, thenExpr.buildFoldRegions(thenExpr.getElement(), document, this));
        }
        if (elseExpr.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, elseExpr.buildFoldRegions(elseExpr.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return true;
    }
}
