package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class AssertNotNullExpression extends Expression implements CheckExpression {
    private final Expression object;

    public AssertNotNullExpression(PsiElement element, TextRange textRange, Expression object) {
        super(element, textRange);
        this.object = object;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@org.jetbrains.annotations.NotNull PsiElement element,
                                                @org.jetbrains.annotations.NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(AssertNotNullExpression.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                object.getTextRange().getEndOffset() < getTextRange().getEndOffset()
                        ? TextRange.create(object.getTextRange().getEndOffset(), getTextRange().getEndOffset())
                        : TextRange.create(getTextRange().getStartOffset(), object.getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "!!";
            }
        });
        if (object.supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
