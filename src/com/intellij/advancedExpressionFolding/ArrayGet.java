package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayGet extends Expression implements GetExpression {
    private final Expression object;
    private final Style style;

    public enum Style {
        FIRST,
        LAST
    }

    public ArrayGet(PsiElement element, TextRange textRange, Expression object, Style style) {
        super(element, textRange);
        this.object = object;
        this.style = style;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && object.getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(ArrayGet.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(object.getTextRange().getEndOffset(),
                        textRange.getEndOffset()), group) {
            @Override
            public String getPlaceholderText() {
                return "." + (style == Style.FIRST ? "first()" : "last()");
            }
        });
        // TODO: Generalize it
        if (object.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
