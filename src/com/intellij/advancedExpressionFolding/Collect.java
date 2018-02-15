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

public class Collect extends Expression {
    private final @NotNull
    Expression qualifier;
    private final @NotNull
    TextRange collectorTextRange;

    public Collect(PsiElement element, TextRange textRange, @NotNull Expression qualifier,
                   @NotNull TextRange collectorTextRange) {
        super(element, textRange);
        this.qualifier = qualifier;
        this.collectorTextRange = collectorTextRange;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        int offset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getStartOffset(), -1);
        return textRange.getStartOffset() + offset < collectorTextRange.getStartOffset()
                && collectorTextRange.getEndOffset() < textRange.getEndOffset();
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(Collect.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        int offset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getStartOffset(), -1);
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset() + offset,
                        collectorTextRange.getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return ".";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(collectorTextRange.getEndOffset(),
                        textRange.getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        if (qualifier.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, qualifier.buildFoldRegions(qualifier.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
