package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CompactControlFlowExpression extends Expression {
    public CompactControlFlowExpression(@NotNull PsiElement element,
                                        @NotNull TextRange textRange) {
        super(element, textRange);
    }

    public static void buildFoldRegions(@NotNull PsiElement element, FoldingGroup group,
                                        ArrayList<FoldingDescriptor> descriptors, TextRange textRange) {
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(),
                        textRange.getStartOffset() + 1), group, ""));
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getEndOffset() - 1,
                        textRange.getEndOffset()), group, ""));
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expression parent) {
        return supportsFoldRegions(document, textRange);
    }

    public static boolean supportsFoldRegions(@NotNull Document document, TextRange textRange) {
        return textRange.getStartOffset() > 0 && textRange.getEndOffset() < document.getTextLength() - 1
                &&
                InterpolatedString.OVERFLOW_CHARACTERS.contains(document.getText(
                        TextRange.create(textRange.getStartOffset() - 1, textRange.getStartOffset())))
                && InterpolatedString.OVERFLOW_CHARACTERS.contains(document.getText(
                TextRange.create(textRange.getEndOffset(), textRange.getEndOffset() + 1)));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        buildFoldRegions(element, FoldingGroup
                        .newGroup(CompactControlFlowExpression.class.getName()
                                + Expression.HIGHLIGHTED_GROUP_POSTFIX),
                descriptors, textRange);
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isHighlighted() {
        return true;
    }
}
