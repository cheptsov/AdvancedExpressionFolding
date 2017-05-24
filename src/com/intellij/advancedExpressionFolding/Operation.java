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

public abstract class Operation extends Expression {
    protected @NotNull String character;
    protected @NotNull List<Expression> operands;
    private int priority;

    public Operation(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String character, int priority, @NotNull List<Expression> operands) {
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

    @NotNull
    public List<Expression> getOperands() {
        return operands;
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getCharacter() {
        return character;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        if (equalOrLessPriority(0)) {
            return false;
        }
        for (int i = 1; i < operands.size(); i++) {
            if (operands.get(i - 1).getTextRange().getEndOffset() >= operands.get(i).getTextRange().getStartOffset()
                    || equalOrLessPriority(i)) {
                return false;
            }
        }
        return true; // TODO no-format: ensure operands.supportFoldRegions
    }

    private boolean equalOrLessPriority(int index) {
        return operands.get(index) instanceof Operation
                && ((Operation) operands.get(index)).getPriority() < priority;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(getClass().getName());
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        int offset = getTextRange().getStartOffset();
        if (operands.get(0).getTextRange().getStartOffset() > offset) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(offset, operands.get(0).getTextRange().getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        offset = operands.get(0).getTextRange().getEndOffset();
        for (int i = 1; i < operands.size(); i++) {
            TextRange r = TextRange.create(offset, operands.get(i).getTextRange().getStartOffset());
            String p = " " + character + " ";
            if (!document.getText(r).equals(p)) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        r, group) {
                    @Override
                    public String getPlaceholderText() {
                        return p;
                    }
                });
            }
            offset = operands.get(i).getTextRange().getEndOffset();
        }
        if (offset < getTextRange().getEndOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(offset, getTextRange().getEndOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, this)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(operand.getElement(), document, this));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        return priority == operation.priority
                && character.equals(operation.character)
                && operands.equals(operation.operands);
    }

    @Override
    public int hashCode() {
        int result = character.hashCode();
        result = 31 * result + priority;
        result = 31 * result + operands.hashCode();
        return result;
    }
}
