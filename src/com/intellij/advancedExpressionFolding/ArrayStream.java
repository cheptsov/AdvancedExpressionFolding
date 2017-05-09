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

public class ArrayStream extends Expression implements StreamsExpression, HighlightingExpression {
    private final Expression argument;

    public ArrayStream(PsiElement element, TextRange textRange, Expression argument) {
        super(element, textRange);
        this.argument = argument;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null && argument != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        int offset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getEndOffset(), 1) + 1;
        final boolean noSpaces = offset == 1;
        FoldingGroup group = FoldingGroup.newGroup(ArrayStream.class.getName() + (noSpaces ? "" : HighlightingExpression.GROUP_POSTFIX));
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(),
                        argument.getTextRange().getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(argument.getTextRange().getEndOffset(),
                        textRange.getEndOffset() + (noSpaces ? 1 : 0)), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return noSpaces ? "." : "";
            }
        });
        if (argument.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, argument.buildFoldRegions(argument.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
