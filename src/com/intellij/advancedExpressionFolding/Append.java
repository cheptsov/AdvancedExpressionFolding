package com.intellij.advancedExpressionFolding;

import java.util.List;

public class Append extends Operation implements ConcatenationExpression {

    public Append(List<Expression> operands) {
        super("+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Append(newOperands);
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
}
