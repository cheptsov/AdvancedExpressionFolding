package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShortElvisExpression extends Expression {
    private final @NotNull Expression thenExpression;
    private final @NotNull List<TextRange> elements;

    public ShortElvisExpression(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expression thenExpression,
                                @NotNull List<TextRange> elements) {
        super(element, textRange);
        this.thenExpression = thenExpression;
        this.elements = elements;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ShortElvisExpression.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(), thenExpression.getTextRange().getStartOffset()),
                group, ""));
        if (thenExpression.getTextRange().getEndOffset() < textRange.getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(thenExpression.getTextRange().getEndOffset(),
                            getTextRange().getEndOffset()), group, ""));
        }
        nullify(element, document, descriptors, group, elements, true);
        if (thenExpression.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, thenExpression.buildFoldRegions(thenExpression.getElement(), document, this));
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
                        group, "?" + postfix));
            } else if (replaceSingle) {
                TextRange r = TextRange.create(range.getStartOffset(), range.getEndOffset());
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        r, group, document.getText(r) + "?"));
            }
        }
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }
}
