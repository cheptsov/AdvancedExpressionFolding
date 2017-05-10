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

public class Setter extends Expression implements GettersSetters {
    private final @NotNull TextRange setterTextRange;
    private final @Nullable Expression object;
    private final @NotNull String name;
    private final @NotNull Expression value;

    public Setter(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull TextRange setterTextRange,
                  @Nullable Expression object, @NotNull String name, @NotNull Expression value) {
        super(element, textRange);
        this.setterTextRange = setterTextRange;
        this.object = object;
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Setter.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(setterTextRange.getStartOffset(),
                                value.getTextRange().getStartOffset()), group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return name + " = ";
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
        if (object != null && object.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document));
        }
        if (value.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, value.buildFoldRegions(value.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
