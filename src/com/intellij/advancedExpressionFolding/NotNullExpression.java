package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class NotNullExpression extends Expression implements CheckExpression {
    private final Expression object;

    public NotNullExpression(TextRange textRange, Expression object) {
        super(textRange);
        this.object = object;
    }

    @Override
    public String format() {
        return object.format();
    }

    /*@Override
    public boolean supportsFoldRegions(Document document) {
        return getTextRange() != null && object.getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@org.jetbrains.annotations.NotNull PsiElement element, @org.jetbrains.annotations.NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(NotNullExpression.class.getName());
        return new FoldingDescriptor[] {
                new FoldingDescriptor(element.getNode(),
                        TextRange.create(object.getTextRange().getEndOffset(),
                                getTextRange().getEndOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                }
        };
    }*/
}
