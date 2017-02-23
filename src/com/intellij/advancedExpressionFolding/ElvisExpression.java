package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElvisExpression extends Expression implements CheckExpression {
    private final Expression conditionExpression;
    private final Expression thenExpression;
    private final Expression elseExpression;
    private final List<TextRange> elements;

    public ElvisExpression(TextRange textRange, Expression conditionExpression, Expression thenExpression,
                           Expression elseExpression,
                           List<TextRange> elements) {
        super(textRange);
        this.conditionExpression = conditionExpression;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
        this.elements = elements;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ElvisExpression.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(), thenExpression.getTextRange().getStartOffset()),
                group) {
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(thenExpression.getTextRange().getEndOffset(),
                        elseExpression.getTextRange().getStartOffset()),
                group) {
            @Override
            public String getPlaceholderText() {
                return " ?: "; // TODO: Eat spaces around
            }
        });
        for (TextRange range : elements) {
            if (".".equals(document.getText(TextRange.create(range.getEndOffset(), range.getEndOffset() + 1)))) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(range.getEndOffset(), range.getEndOffset() + 1),
                        group) {
                    @Override
                    public String getPlaceholderText() {
                        return "?.";
                    }
                });
            }
        }
        if (thenExpression.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, thenExpression.buildFoldRegions(element, document));
        }
        if (elseExpression.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, elseExpression.buildFoldRegions(element, document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null
                && thenExpression.getTextRange() != null
                && elseExpression.getTextRange() != null;
    }

    @Override
    public String format() {
        // TODO: Get rid out of format completely
        return conditionExpression.format() + " ? " + thenExpression.format() + " : " + elseExpression;
    }
}
