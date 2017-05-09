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

    public String getCharacter() {
        return character;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null
                /*&& operands.size() >= 2*/
                && operands.stream().allMatch(operand -> operand.getTextRange() != null
                    && !(operand instanceof Operation)
                    /*&& !(operand instanceof Function)*/)
                /*&& (quick || !format().equals(document.getText(getTextRange())))*/; // TODO no-format: ensure operands.supportFoldRegions
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
