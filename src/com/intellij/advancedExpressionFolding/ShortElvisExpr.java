package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShortElvisExpr extends Expr implements CheckExpression {
    private final @NotNull
    Expr thenExpr;
    private final @NotNull List<TextRange> elements;

    public ShortElvisExpr(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expr thenExpr,
                          @NotNull List<TextRange> elements) {
        super(element, textRange);
        this.thenExpr = thenExpr;
        this.elements = elements;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expr parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ShortElvisExpr.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(), thenExpr.getTextRange().getStartOffset()),
                group) {
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        if (thenExpr.getTextRange().getEndOffset() < textRange.getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(thenExpr.getTextRange().getEndOffset(),
                            getTextRange().getEndOffset()),
                    group) {
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        nullify(element, document, descriptors, group, elements, true);
        if (thenExpr.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, thenExpr.buildFoldRegions(thenExpr.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    protected static Set<String> supportedPostfixes = new HashSet<String>() {
        {
            add(".");
            add(";");
            add(",");
            add(")");
        }
    };

    protected static void nullify(@NotNull PsiElement element, @NotNull Document document,
                                  ArrayList<FoldingDescriptor> descriptors, FoldingGroup group,
                                  List<TextRange> elements, boolean replaceSingle) {
        for (TextRange range : elements) {
            String postfix = document.getText(TextRange.create(range.getEndOffset(), range.getEndOffset() + 1));
            if (supportedPostfixes.contains(postfix)) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(range.getEndOffset(), range.getEndOffset() + 1),
                        group) {
                    @Override
                    public String getPlaceholderText() {
                        return "?" + postfix;
                    }
                });
            } else if (replaceSingle) {
                TextRange r = TextRange.create(range.getStartOffset(), range.getEndOffset());
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        r,
                        group) {
                    @Override
                    public String getPlaceholderText() {
                        return document.getText(r) + "?";
                    }
                });
            }
        }
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return true;
    }
}
