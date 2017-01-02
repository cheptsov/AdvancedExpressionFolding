package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class Divide extends Operation implements ArithmeticExpression {
    private final boolean isCollapsedByDefault;

    public Divide(TextRange textRange, List<Expression> operands) {
        this(textRange, operands, true);
    }

    public Divide(TextRange textRange, List<Expression> operands, boolean isCollapsedByDefault) {
        super(textRange, "/", 100, operands);
        this.isCollapsedByDefault = isCollapsedByDefault;
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Divide(textRange, newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public boolean isCollapsedByDefault() {
        return isCollapsedByDefault;
    }

    @Override
    public String format() {
        if (operands.size() == 2 && operands.get(0) instanceof NumberLiteral && operands.get(1) instanceof NumberLiteral) {
            String format = format(((NumberLiteral) operands.get(0)).getNumber().doubleValue() / ((NumberLiteral) operands.get(1)).getNumber().doubleValue());
            if (format != null) {
                return format;
            }
        }
        return super.format();
    }
}
