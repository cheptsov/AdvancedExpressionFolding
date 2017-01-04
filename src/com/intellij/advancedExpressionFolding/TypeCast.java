package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypeCast extends Expression implements CastExpression {
    private final Expression object;

    public TypeCast(TextRange textRange, Expression object) {
        super(textRange);
        this.object = object;
    }

    @Override
    public String format() {
        return object.format();
    }

    public Expression getObject() {
        return object;
    }

    @Override
    public boolean supportsFoldRegions(Document document) {
        return getTextRange() != null && object.getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(Contains.class.getName());
        boolean dotAccess = document.getText(TextRange.create(getTextRange().getEndOffset(),
                getTextRange().getEndOffset() + 1)).equals(".");
        return new FoldingDescriptor[]{
                dotAccess ?
                        new FoldingDescriptor(element.getNode(),
                                TextRange.create(getTextRange().getStartOffset(),
                                        object.getTextRange().getStartOffset()), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "";
                            }
                        }
                        : new FoldingDescriptor(element.getNode(),
                                TextRange.create(getTextRange().getStartOffset(),
                                        object.getTextRange().getStartOffset()), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "~";
                            }
                        }
                        ,
                dotAccess ?
                        new FoldingDescriptor(element.getNode(),
                                TextRange.create(object.getTextRange().getEndOffset(),
                                        getTextRange().getEndOffset() + 1), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return ".";
                            }
                        }
                        : new FoldingDescriptor(element.getNode(),
                                TextRange.create(object.getTextRange().getEndOffset(),
                                        getTextRange().getEndOffset()), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "";
                            }
                        }
        };
    }
}
