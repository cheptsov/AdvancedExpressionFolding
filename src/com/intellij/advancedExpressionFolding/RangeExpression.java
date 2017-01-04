package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RangeExpression extends Expression implements ComparingExpression {
    public static final String RANGE_COMMA_DELIMITER = ", ";
    public static final String RANGE_IN_SEPARATOR = "in";

    private Expression operand;
    private Expression startRange;
    private Expression endRange;
    protected String separator;
    private boolean startInclusive;
    private boolean endInclusive;

    public RangeExpression(TextRange textRange, Expression operand, Expression startRange, boolean startInclusive,
                           Expression endRange,
                           boolean endInclusive) {
        super(textRange);
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

        RangeExpression that = (RangeExpression) o;

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

    @Override
    public Expression simplify(boolean compute) {
        Expression sOperator = operand.simplify(compute);
        Expression sStartRange = startRange.simplify(compute);
        Expression sEndRange = endRange.simplify(compute);
        if (sOperator != operand || sStartRange != startRange || sEndRange != endRange) {
            return new RangeExpression(textRange, sOperator, sStartRange, startInclusive, sEndRange, endInclusive);
        } else {
            return this;
        }
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder().append(operand.format()).append(" ").append(separator).append(" ");
        if (startInclusive) {
            sb.append("[");
        } else {
            sb.append("(");
        }
        sb.append(startRange.format());
        sb.append(RANGE_COMMA_DELIMITER);
        sb.append(endRange.format());
        if (endInclusive) {
            sb.append("]");
        } else {
            sb.append(")");
        }
        return sb.toString();
    }

    public Expression getStart() {
        return startRange;
    }

    public Expression getOperand() {
        return operand;
    }

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
    public boolean supportsFoldRegions(Document document) {
        return getOperand().getTextRange() != null
                && getStart().getTextRange() != null
                && getEnd().getTextRange() != null
                && getTextRange() != null
                && getStart().getTextRange().getStartOffset() < getEnd().getTextRange().getStartOffset()
                && (getEnd().getTextRange().getEndOffset() < getTextRange().getEndOffset()
                    || supportedOverlappedSymbols.contains(document.getText(TextRange.create(getEnd().getTextRange().getEndOffset(), getEnd().getTextRange().getEndOffset() + 1))));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(ForStatementExpression.class.getName());
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
        return new FoldingDescriptor[] {
                new FoldingDescriptor(element.getNode(),
                        TextRange.create(getOperand().getTextRange().getEndOffset(),
                                getStart().getTextRange().getStartOffset()),
                        group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return p1;
                    }
                },
                new FoldingDescriptor(element.getNode(),
                        TextRange.create(getStart().getTextRange().getEndOffset(),
                                getEnd().getTextRange().getStartOffset()),
                        group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return RANGE_COMMA_DELIMITER;
                    }
                },
                getTextRange().getEndOffset() > getEnd().getTextRange().getEndOffset() ?
                new FoldingDescriptor(element.getNode(),
                        TextRange.create(getEnd().getTextRange().getEndOffset(),
                                getTextRange().getEndOffset()),
                        group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return p2;
                    }
                } : new FoldingDescriptor(element.getNode(),
                        TextRange.create(getEnd().getTextRange().getEndOffset(),
                                getEnd().getTextRange().getEndOffset() + 1),
                        group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return p2 + document.getText(TextRange.create(getEnd().getTextRange().getEndOffset(), getEnd().getTextRange().getEndOffset() + 1));
                    }
                }

        };
    }
}
