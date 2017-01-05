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

public class Contains extends Expression implements CheckExpression {
    private final Expression object;
    private final Expression key;

    public Contains(TextRange textRange, Expression object, Expression key) {
        super(textRange);
        this.object = object;
        this.key = key;
    }

    @Override
    public String format() {
        return object.format() + "[" + key + "]?";
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && object.getTextRange() != null && key.getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Contains.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(object.getTextRange().getEndOffset(),
                        key.getTextRange().getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "[";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(key.getTextRange().getEndOffset(),
                        getTextRange().getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "]?";
            }
        });
        if (object.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, object.buildFoldRegions(element, document));
        }
        if (key.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, key.buildFoldRegions(element, document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
