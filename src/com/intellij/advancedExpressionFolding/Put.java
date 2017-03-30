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
    private final Expression object;
    private final Expression key;
    private final Expression value;

    public Put(PsiElement element, TextRange textRange, Expression object, Expression key, Expression value) {
        super(element, textRange);
        this.object = object;
        this.key = key;
        this.value = value;
    }

    @Override
    public String format() {
        return object.format() + "[" + key.format() + "] = " + value.format();
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && object.getTextRange() != null && key.getTextRange() != null && value.getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Put.class.getName());
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
                                value.getTextRange().getStartOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "] = ";
                    }
                });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                                TextRange.create(value.getTextRange().getEndOffset(),
                                        getTextRange().getEndOffset()), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "";
                            }
                        });
        if (object.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document));
        }
        if (key.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, key.buildFoldRegions(key.getElement(), document));
        }
        if (value.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, value.buildFoldRegions(value.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
