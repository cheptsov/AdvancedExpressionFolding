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

public class Setter extends Expression implements GetExpression {
    private final TextRange setterTextRange;
    private final Expression object;
    private final String name;
    private final Expression value;

    public Setter(TextRange textRange, TextRange setterTextRange, Expression object, String name, Expression value) {
        super(textRange);
        this.setterTextRange = setterTextRange;
        this.object = object;
        this.name = name;
        this.value = value;
    }

    @Override
    public String format() {
        return object.format() + "." + name + " = " + value.format();
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && object.getTextRange() != null && value.getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Setter.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(setterTextRange.getStartOffset(),
                                value.getTextRange().getStartOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return name + " = ";
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
            Collections.addAll(descriptors, object.buildFoldRegions(element, document));
        }
        if (value.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, value.buildFoldRegions(element, document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
