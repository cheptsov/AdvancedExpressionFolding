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

public class Put extends Expression implements GetExpression {
    private final @NotNull Expression object;
    private final @NotNull Expression key;
    private final @NotNull Expression value;

    public Put(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expression object, @NotNull Expression key, @NotNull Expression value) {
        super(element, textRange);
        this.object = object;
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return this.object.getTextRange().getEndOffset() < this.key.getTextRange().getStartOffset(); // TODO: Check how this is possible
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(Put.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(object.getTextRange().getEndOffset(),
                                key.getTextRange().getStartOffset()), group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return "[";
                    }
                });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(key.getTextRange().getEndOffset(),
                                value.getTextRange().getStartOffset()), group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return "] = ";
                    }
                });
        if (value.getTextRange().getEndOffset() < getTextRange().getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(value.getTextRange().getEndOffset(),
                            getTextRange().getEndOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        if (object.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document, this));
        }
        if (key.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, key.buildFoldRegions(key.getElement(), document, this));
        }
        if (value.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, value.buildFoldRegions(value.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
