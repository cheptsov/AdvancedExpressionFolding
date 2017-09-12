package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StreamExpr extends Expr implements StreamsExpression {
    public StreamExpr(@NotNull PsiElement element, @NotNull TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expr parent) {
        int startOffset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getStartOffset(), -1);
        int endOffset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getEndOffset(), 1) + 1;
        final boolean noSpaces = endOffset == 1;
        return new FoldingDescriptor[] {
                new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset() + startOffset,
                        textRange.getEndOffset() + (noSpaces ? 1 : 0)),
                        FoldingGroup.newGroup(StreamExpr.class.getName() + (noSpaces ? "" : Expr.HIGHLIGHTED_GROUP_POSTFIX))) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return noSpaces ? "." : "";
                    }
                }
        };
    }

    @Override
    public boolean isHighlighted() {
        return true;
    }
}
