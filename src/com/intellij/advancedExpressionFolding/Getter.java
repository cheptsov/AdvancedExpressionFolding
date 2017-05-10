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

public class Getter extends Expression implements GettersSetters {
    private @NotNull String name;
    private @NotNull TextRange getterTextRange;
    private @Nullable Expression object;

    public Getter(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull TextRange getterTextRange, @Nullable Expression object, @NotNull String name) {
        super(element, textRange);
        this.getterTextRange = getterTextRange;
        this.object = object;
        this.name = name;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(
                new FoldingDescriptor(element.getNode(), getterTextRange,
                        FoldingGroup.newGroup(Getter.class.getName())) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return name;
                    }
                }
        );
        if (object != null && object.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
