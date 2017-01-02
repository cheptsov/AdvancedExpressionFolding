package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.List;

public class Append extends Operation implements ConcatenationExpression {

    public Append(TextRange textRange, List<Expression> operands) {
        super(textRange, "+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Append(textRange, newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public String format() {
        String format = super.format();
        if (operands.get(0) instanceof Variable) {
            format = format.replaceFirst("\\+", "+=");
        } else if (operands.get(0) instanceof StringLiteral && ((StringLiteral) operands.get(0)).getString().equals("")) {
            format = format.replaceFirst("\"\" \\+ ", "");
        }
        return format;
    }

    @Override
    public boolean isCollapsedByDefault() {
        if (!super.isCollapsedByDefault()) {
            return false;
        }
        for (Expression operand : operands) {
            if (operand instanceof Add && ((Add) operand).getOperands().stream()
                    .anyMatch(o -> !(o instanceof StringLiteral))) {
                return false;
            }
        }
        return true;
    }
}
