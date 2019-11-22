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

public class ElvisExpression extends Expression {
    private final @NotNull Expression thenExpression;
    private final @NotNull Expression elseExpression;
    private final @NotNull List<TextRange> elements;

    public ElvisExpression(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expression thenExpression,
                           @NotNull Expression elseExpression,
                           @NotNull List<TextRange> elements) {
        super(element, textRange);
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
        this.elements = elements;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ElvisExpression.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(), thenExpression.getTextRange().getStartOffset()),
                group, ""));
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(thenExpression.getTextRange().getEndOffset(),
                        elseExpression.getTextRange().getStartOffset()),
                group, " ?: " /* TODO: Eat spaces around */));
        ShortElvisExpression.nullify(element, document, descriptors, group, elements,
                !(elements.size() == 1 && elements.get(0).equals(thenExpression.getTextRange())));
        if (thenExpression.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, thenExpression.buildFoldRegions(thenExpression.getElement(), document, this));
        }
        if (elseExpression.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, elseExpression.buildFoldRegions(elseExpression.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }
}
