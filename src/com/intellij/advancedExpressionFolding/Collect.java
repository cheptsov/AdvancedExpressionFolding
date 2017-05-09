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

public class Collect extends Expression implements StreamsExpression {
    private final Expression qualifier;
    private final TextRange collectorTextRange;

    public Collect(PsiElement element, TextRange textRange, Expression qualifier, TextRange collectorTextRange) {
        super(element, textRange);
        this.qualifier = qualifier;
        this.collectorTextRange = collectorTextRange;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null && qualifier != null && collectorTextRange != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Collect.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        int offset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getStartOffset(), -1);
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset() + offset,
                        collectorTextRange.getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return ".";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(collectorTextRange.getEndOffset(),
                        textRange.getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        if (qualifier.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, qualifier.buildFoldRegions(qualifier.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
