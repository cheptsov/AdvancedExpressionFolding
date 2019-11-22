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
import java.util.HashSet;
import java.util.Set;

public class Range extends Expression {
    public static final String RANGE_COMMA_DELIMITER = ", ";
    public static final String RANGE_IN_SEPARATOR = "in";

    private @NotNull
    Expression operand;
    private @NotNull
    Expression startRange;
    private @NotNull
    Expression endRange;
    String separator;
    private boolean startInclusive;
    private boolean endInclusive;

    public Range(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull Expression operand, @NotNull Expression startRange, boolean startInclusive,
                 @NotNull Expression endRange, boolean endInclusive) {
        super(element, textRange);
        this.operand = operand;
        this.startRange = startRange;
        this.startInclusive = startInclusive;
        this.endRange = endRange;
        this.endInclusive = endInclusive;
        this.separator = RANGE_IN_SEPARATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range that = (Range) o;

        if (startInclusive != that.startInclusive) return false;
        if (endInclusive != that.endInclusive) return false;
        if (!operand.equals(that.operand)) return false;
        if (!startRange.equals(that.startRange)) return false;
        if (!endRange.equals(that.endRange)) return false;
        return separator.equals(that.separator);
    }

    @Override
    public int hashCode() {
        int result = operand.hashCode();
        result = 31 * result + startRange.hashCode();
        result = 31 * result + endRange.hashCode();
        result = 31 * result + separator.hashCode();
        result = 31 * result + (startInclusive ? 1 : 0);
        result = 31 * result + (endInclusive ? 1 : 0);
        return result;
    }

    public boolean isStartInclusive() {
        return startInclusive;
    }

    public boolean isEndInclusive() {
        return endInclusive;
    }

    @NotNull
    public Expression getStart() {
        return startRange;
    }

    @NotNull
    public Expression getOperand() {
        return operand;
    }

    @NotNull
    public Expression getEnd() {
        return endRange;
    }

    private static Set<String> supportedOverlappedSymbols = new HashSet<String>() {
        {
            add(" ");
            add("&");
            add("|");
            add("(");
            add(")");
        }
    };

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return getStart().getTextRange().getStartOffset() < getEnd().getTextRange().getStartOffset()
                && (getEnd().getTextRange().getEndOffset() < getTextRange().getEndOffset()
                || supportedOverlappedSymbols.contains(document.getText(TextRange.create(getEnd().getTextRange().getEndOffset(), getEnd().getTextRange().getEndOffset() + 1))));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        StringBuilder sb1 = new StringBuilder().append(" ").append(separator).append(" ");
        if (isStartInclusive()) {
            sb1.append("[");
        } else {
            sb1.append("(");
        }
        String p1 = sb1.toString();
        StringBuilder sb2 = new StringBuilder();
        if (isEndInclusive()) {
            sb2.append("]");
        } else {
            sb2.append(")");
        }
        String p2 = sb2.toString();
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(getOperand().getTextRange().getEndOffset(),
                        getStart().getTextRange().getStartOffset()),
                group, p1));
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(getStart().getTextRange().getEndOffset(),
                        getEnd().getTextRange().getStartOffset()),
                group, RANGE_COMMA_DELIMITER));
        descriptors.add(getTextRange().getEndOffset() > getEnd().getTextRange().getEndOffset() ?
                new FoldingDescriptor(element.getNode(),
                        TextRange.create(getEnd().getTextRange().getEndOffset(),
                                getTextRange().getEndOffset()),
                        group, p2) : new FoldingDescriptor(element.getNode(),
                TextRange.create(getEnd().getTextRange().getEndOffset(),
                        getEnd().getTextRange().getEndOffset() + 1),
                group, p2 + document.getText(TextRange
                .create(getEnd().getTextRange().getEndOffset(), getEnd().getTextRange().getEndOffset() + 1)))
        );
        if (startRange.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, startRange.buildFoldRegions(startRange.getElement(), document, this));
        }
        if (endRange.supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, endRange.buildFoldRegions(endRange.getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
