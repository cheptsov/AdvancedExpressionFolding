package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class NumberLiteral extends Expression implements ArithmeticExpression {
    private @NotNull Number number;
    private final boolean convert;
    private @Nullable TextRange numberTextRange;

    public NumberLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @Nullable TextRange numberTextRange, @NotNull Number number, boolean convert) {
        super(element, textRange);
        this.numberTextRange = numberTextRange;
        this.number = number;
        this.convert = convert;
    }

    @NotNull
    public Number getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberLiteral numberLiteral = (NumberLiteral) o;

        return number.toString().equals(numberLiteral.number.toString());
    }

    @Override
    public int hashCode() {
        return number.toString().hashCode();
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return isHighlighted();
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>(3);
        //noinspection Duplicates
        if (numberTextRange != null) {
            FoldingGroup group = FoldingGroup
                    .newGroup(NumberLiteral.class.getName() + Expression.HIGHLIGHTED_GROUP_POSTFIX);
            if (textRange.getStartOffset() < numberTextRange.getStartOffset()) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(textRange.getStartOffset(), numberTextRange.getStartOffset()), group, ""));
            }
            if (convert) {
                String numberLiteral = number.toString();
                if (number instanceof Float) {
                    numberLiteral = number.toString() + 'f';
                }
                descriptors.add(new FoldingDescriptor(element.getNode(), numberTextRange, group, numberLiteral));
            }
            if (numberTextRange.getEndOffset() < textRange.getEndOffset()) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(numberTextRange.getEndOffset(), textRange.getEndOffset()), group, ""));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isHighlighted() {
        return numberTextRange != null && numberTextRange.getStartOffset() >= textRange.getStartOffset()
                && numberTextRange.getEndOffset() <= textRange.getEndOffset();
    }

    @Nullable
    public TextRange getNumberTextRange() {
        return numberTextRange;
    }
}
