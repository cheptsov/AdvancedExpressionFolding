package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

public class RangeExpression extends Expression implements ComparingExpression {
    public static final String RANGE_COMMA_DELIMITER = ", ";
    public static final String RANGE_IN_SEPARATOR = "in";

    private Expression operand;
    private Expression startRange;
    private Expression endRange;
    private String separator;
    private boolean startInclusive;
    private boolean endInclusive;

    public RangeExpression(TextRange textRange, Expression operand, Expression startRange, boolean startInclusive, Expression endRange,
                           boolean endInclusive, String separator) {
        super(textRange);
        this.operand = operand;
        this.startRange = startRange;
        this.startInclusive = startInclusive;
        this.endRange = endRange;
        this.endInclusive = endInclusive;
        this.separator = separator;
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

    public String getSeparator() {
        return separator;
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
            return new RangeExpression(textRange, sOperator, sStartRange, startInclusive, sEndRange, endInclusive, separator);
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

    public Expression getStartRange() {
        return startRange;
    }

    public Expression getOperand() {
        return operand;
    }

    public Expression getEndRange() {
        return endRange;
    }


}
