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

public class ArrayGet extends Expr implements GetExpression {
    private final Expr object;
    private final Style style;

    public enum Style {
        FIRST,
        LAST
    }

    public ArrayGet(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expr object, @NotNull Style style) {
        super(element, textRange);
        this.object = object;
        this.style = style;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expr parent) {
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
        if (object.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
