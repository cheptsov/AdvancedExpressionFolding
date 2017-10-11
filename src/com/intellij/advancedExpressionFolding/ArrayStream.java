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

public class ArrayStream extends Expression {
    private final @NotNull Expression argument;

    public ArrayStream(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expression argument) {
        super(element, textRange);
        this.argument = argument;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        int offset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getEndOffset(), 1) + 1;
        final boolean noSpaces = offset == 1;
        FoldingGroup group = FoldingGroup.newGroup(ArrayStream.class.getName() + (noSpaces ? "" : Expression.HIGHLIGHTED_GROUP_POSTFIX));
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(),
                        argument.getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(argument.getTextRange().getEndOffset(),
                        textRange.getEndOffset() + (noSpaces ? 1 : 0)), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return noSpaces ? "." : "";
            }
        });
        if (argument.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, argument.buildFoldRegions(argument.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isHighlighted() {
        return true;
    }
}
