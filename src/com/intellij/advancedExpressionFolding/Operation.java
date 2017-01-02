package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.util.TextRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Operation extends Expression {
    protected String character;
    protected List<Expression> operands;
    private int priority;

    public Operation(TextRange textRange, String character, int priority, List<Expression> operands) {
        super(textRange);
        this.character = character;
        this.priority = priority;
        this.operands = operands;
    }

    @Override
    public boolean isCollapsedByDefault() {
        for (Expression operand : operands) {
            if (!operand.isCollapsedByDefault()) {
                return false;
            }
        }
        return super.isCollapsedByDefault();
    }

    public List<Expression> getOperands() {
        return operands;
    }

    @Override
    public Expression simplify(boolean compute) {
        List<Expression> simplifiedOperands = null;
        for (int i = 0; i < operands.size(); i++) {
            Expression operand = operands.get(i);
            boolean toSimplify = false;
            if (operand instanceof Operation) {
                Operation simplifiedOperand = (Operation) operand;
                Expression s = simplifiedOperand.simplify(compute);
                if (s != simplifiedOperand) {
                    toSimplify = true;
                    if (s instanceof Operation) {
                        simplifiedOperand = (Operation) s;
                        if (simplifiedOperands == null) {
                            simplifiedOperands = new ArrayList<>();
                            if (i > 0) {
                                simplifiedOperands.addAll(operands.subList(0, i));
                            }
                        }
                    } else {
                        operand = s;
                    }
                }
                if (Objects.equals(this.getCharacter(), simplifiedOperand.getCharacter())) {
                    if (simplifiedOperands == null) {
                        simplifiedOperands = new ArrayList<>();
                        if (i > 0) {
                            simplifiedOperands.addAll(operands.subList(0, i));
                        }
                    }
                    simplifiedOperands.addAll(simplifiedOperand.operands);
                    toSimplify = true;
                } else if (toSimplify) {
                    simplifiedOperands.add(operand);
                }
            }
            if (!toSimplify && simplifiedOperands != null) {
                simplifiedOperands.add(operand);
            }
        }
        if (simplifiedOperands != null) {
            return copy(simplifiedOperands);
        }
        return this;
    }

    abstract Expression copy(List<Expression> newOperands);

    private int compareTo(Operation operation) {
        return this.getPriority() == operation.getPriority()
                ? 0
                : this.getPriority() > operation.getPriority()
                ? 1
                : -1;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operands.size(); i++) {
            Expression operand = operands.get(i);
            String f = operand.format();
            boolean toSimplify = false;
            if (operand instanceof Operation) {
                Operation o = (Operation) operand;
                toSimplify = (i == 0 || (o.isAssociative() && this.isAssociative())) && o.compareTo(this) >= 0
                        || o.compareTo(this) > 0;
            }
            if (operand instanceof Operation && !toSimplify) {
                sb.append("(");
            }
            sb.append(f);
            if (operand instanceof Operation && !toSimplify) {
                sb.append(")");
            }
            if (i < operands.size() - 1) {
                sb.append(" ");
            }
            if (i < operands.size() - 1) {
                sb.append(character).append(" ");
            }
        }
        return sb.toString();
    }

    public String getCharacter() {
        return character;
    }

    public boolean isAssociative() {
        return true;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (priority != operation.priority) return false;
        if (!character.equals(operation.character)) return false;
        return operands.equals(operation.operands);
    }

    @Override
    public int hashCode() {
        int result = character.hashCode();
        result = 31 * result + priority;
        result = 31 * result + operands.hashCode();
        return result;
    }
}
