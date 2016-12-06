package com.intellij.bigdecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Operation extends Expression {
    protected String character;
    protected List<Expression> operands;
    private boolean assosiative;

    public Operation(String character, List<Expression> operands) {
        this.character = character;
        this.operands = operands;
    }

    public List<Expression> getOperands() {
        return operands;
    }

    @Override
    public Operation simplify() {
        List<Expression> simplifiedOperands = null;
        for (int i = 0; i < operands.size(); i++) {
            boolean toSimplify = false;
            Expression operand = operands.get(i);
            if (operand instanceof Operation) {
                Operation simplifiedOperand = (Operation) operand;
                Operation s = simplifiedOperand.simplify();
                if (s != simplifiedOperand) {
                    simplifiedOperand = s;
                    toSimplify = true;
                    if (simplifiedOperands == null) {
                        simplifiedOperands = new ArrayList<>();
                        if (i > 0) {
                            simplifiedOperands.addAll(operands.subList(0, i));
                        }
                    }
                }
                if ((i == 0 || simplifiedOperand.isAssociative()) && simplifiedOperand.compareTo(this) >= 0) {
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
            return (Operation) copy(simplifiedOperands);
        }
        return this;
    }

    protected abstract int compareTo(Operation operation);

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operands.size(); i++) {
            Expression operand = operands.get(i);
            String f = operand.format();
            boolean toSimplify = false;
            if (operand instanceof Operation) {
                Operation o = (Operation) operand;
                toSimplify = true;
                if (i > 0 && !o.isAssociative() || o.compareTo(this) < 0) {
                    toSimplify = false;
                }
            }
            if (!skilHeadingZero() && (i != 0 || !(operand instanceof Literal) || !Objects.equals(((Literal) operand)
                    .getNumber(), BigDecimal.ZERO))) {
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

    protected boolean skilHeadingZero() {
        return false;
    }

    public boolean isAssociative() {
        return true;
    }
}
