package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShortElvisExpression extends Expression implements CheckExpression {
    private final Expression conditionExpression;
    private final Expression thenExpression;
    private final List<TextRange> elements;

    public ShortElvisExpression(TextRange textRange, Expression conditionExpression, Expression thenExpression,
                                List<TextRange> elements) {
        super(textRange);
        this.conditionExpression = conditionExpression;
        this.thenExpression = thenExpression;
        this.elements = elements;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ShortElvisExpression.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(), thenExpression.getTextRange().getStartOffset()),
                group) {
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        if (thenExpression.getTextRange().getEndOffset() < textRange.getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(thenExpression.getTextRange().getEndOffset(),
                            getTextRange().getEndOffset()),
                    group) {
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        nullify(element, document, descriptors, group, elements, true);
        if (thenExpression.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, thenExpression.buildFoldRegions(element, document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    protected static Set<String> supportedPostfixes = new HashSet<String>() {
        {
            add(".");
            add(";");
            add(",");
            add(")");
        }
    };

    protected static void nullify(@NotNull PsiElement element, @NotNull Document document,
                                  ArrayList<FoldingDescriptor> descriptors, FoldingGroup group,
                                  List<TextRange> elements, boolean replaceSingle) {
        for (TextRange range : elements) {
            String postfix = document.getText(TextRange.create(range.getEndOffset(), range.getEndOffset() + 1));
            if (supportedPostfixes.contains(postfix)) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(range.getEndOffset(), range.getEndOffset() + 1),
                        group) {
                    @Override
                    public String getPlaceholderText() {
                        return "?" + postfix;
                    }
                });
            } else if (replaceSingle) {
                TextRange r = TextRange.create(range.getStartOffset(), range.getEndOffset());
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        r,
                        group) {
                    @Override
                    public String getPlaceholderText() {
                        return document.getText(r) + "?";
                    }
                });
            }
        }
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null && thenExpression.getTextRange() != null;
    }

    @Override
    public String format() {
        // TODO: Get rid out of format completely
        return "if (" + conditionExpression.format() + ") " + thenExpression.format();
    }
}
