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

public class TypeCast extends Expression {
    private final @NotNull Expression object;

    public TypeCast(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expression object) {
        super(element, textRange);
        this.object = object;
    }

    @NotNull
    public Expression getObject() {
        return object;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        boolean dotAccess = document.getTextLength() > getTextRange().getEndOffset()
                && document.getText(TextRange.create(getTextRange().getEndOffset(), getTextRange().getEndOffset() + 1)).equals(".");
        FoldingGroup group = FoldingGroup.newGroup(TypeCast.class.getName() + (dotAccess ? "" : Expression.HIGHLIGHTED_GROUP_POSTFIX));
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        if (object.getTextRange().getEndOffset() < getTextRange().getEndOffset()) {
            if (dotAccess) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(getTextRange().getStartOffset(),
                                object.getTextRange().getStartOffset()), group, ""));
                descriptors.add(new FoldingDescriptor(element.getNode(),
                                        TextRange.create(object.getTextRange().getEndOffset(),
                                                getTextRange().getEndOffset() + 1), group, ".")
                );
            } else {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                                        TextRange.create(getTextRange().getStartOffset(),
                                                object.getTextRange().getStartOffset()), group,
                        "" /* TODO: It used to be  "~" */)
                );
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(object.getTextRange().getEndOffset(),
                                getTextRange().getEndOffset()), group, ""));
            }
        } else {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(getTextRange().getStartOffset(),
                            object.getTextRange().getStartOffset()), group, "" /* TODO: It used to be  "~" */));
        }
        if (object.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, object.buildFoldRegions(object.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isHighlighted() {
        return true;
    }
}
