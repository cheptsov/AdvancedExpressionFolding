package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StreamExpression extends Expression {
    public StreamExpression(@NotNull PsiElement element, @NotNull TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        int startOffset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getStartOffset(), -1, true);
        int endOffset = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getEndOffset() - 1, 1, false);
        if (startOffset < -1
                && document.getText(TextRange.create(textRange.getStartOffset() + startOffset, textRange.getStartOffset() + startOffset + 1)).equals("\n")
                && endOffset > 1) {
            int startOffsetNoWhitespace = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getStartOffset(), -1, false);
            return new FoldingDescriptor[]{
                    new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset() + startOffsetNoWhitespace,
                            textRange.getEndOffset() + endOffset),
                            FoldingGroup.newGroup(StreamExpression.class.getName())) {
                        @NotNull
                        @Override
                        public String getPlaceholderText() {
                            return ".";
                        }
                    }
            };
        } else if (startOffset < -1
                && document.getText(TextRange.create(textRange.getStartOffset() + startOffset, textRange.getStartOffset() + startOffset + 1)).equals(".")) {
            int endOffsetWithWhitespace = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getEndOffset() - 1, 1, true);
            return new FoldingDescriptor[]{
                    new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset(),
                            textRange.getEndOffset() + endOffsetWithWhitespace),
                            FoldingGroup.newGroup(StreamExpression.class.getName() + HIGHLIGHTED_GROUP_POSTFIX)) {
                        @NotNull
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    }
            };
        } else if (startOffset == -1
                && endOffset > 1) {
            return new FoldingDescriptor[]{
                    new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset() + startOffset,
                            textRange.getEndOffset()),
                            FoldingGroup.newGroup(StreamExpression.class.getName() + Expression.HIGHLIGHTED_GROUP_POSTFIX)) {
                        @NotNull
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    }
            };
        } else if (startOffset < -1
                && document.getText(TextRange.create(textRange.getStartOffset() - 1, textRange.getStartOffset())).equals(".")
                && endOffset == 1) {
            int endOffsetWithWhitespace = AdvancedExpressionFoldingBuilder.findDot(document, textRange.getEndOffset() - 1, 1, true);
            return new FoldingDescriptor[]{
                    new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset() - 1,
                            textRange.getEndOffset() + endOffsetWithWhitespace),
                            FoldingGroup.newGroup(StreamExpression.class.getName())) {
                        @NotNull
                        @Override
                        public String getPlaceholderText() {
                            return ".";
                        }
                    }
            };
        } else if (startOffset == -1
                && endOffset == 1) {
            return new FoldingDescriptor[]{
                    new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset() - 1,
                            textRange.getEndOffset() + 1),
                            FoldingGroup.newGroup(StreamExpression.class.getName())) {
                        @NotNull
                        @Override
                        public String getPlaceholderText() {
                            return ".";
                        }
                    }
            };
        }
        return FoldingDescriptor.EMPTY;
    }

    @Override
    public boolean isHighlighted() {
        return true;
    }
}
