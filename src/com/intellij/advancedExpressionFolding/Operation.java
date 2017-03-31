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
import java.util.List;
import java.util.Objects;

public abstract class Operation extends Expression {
    protected String character;
    protected List<Expression> operands;
    private int priority;

    public Operation(PsiElement element, TextRange textRange, String character, int priority, List<Expression> operands) {
        super(element, textRange);
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
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null
                /*&& operands.size() >= 2*/
                && operands.stream().allMatch(operand -> operand.getTextRange() != null
                    && !(operand instanceof Operation)
                    /*&& !(operand instanceof Function)*/)
                && (quick || !format().equals(document.getText(getTextRange())));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        int offset = getTextRange().getStartOffset();
        if (operands.get(0).getTextRange().getStartOffset() > offset) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(offset, operands.get(0).getTextRange().getStartOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        offset = operands.get(0).getTextRange().getEndOffset();
        for (int i = 1; i < operands.size(); i++) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(offset, operands.get(i).getTextRange().getStartOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return " " + character + " ";
                }
            });
            offset = operands.get(i).getTextRange().getEndOffset();
        }
        if (offset < getTextRange().getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(offset, getTextRange().getEndOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, false)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(operand.getElement(), document));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
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
