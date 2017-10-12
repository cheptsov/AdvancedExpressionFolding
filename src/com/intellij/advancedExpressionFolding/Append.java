package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * TODO: sb.append(interpolatedString).append(x) - merge x into interpolatedString
 * TODO: merge multiple sb.append() into a single append(interpolatedString)
 */
public class Append extends Operation {
    public Append(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expression> operands) {
        super(element, textRange, "+", 10, operands);
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

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expression parent) {
        return super.supportsFoldRegions(document, parent) & operands.size() > 0;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        // TODO: Generalize this code for Operation
        FoldingGroup group = FoldingGroup.newGroup(Append.class.getName() +
                (operands.size() == 1 ? HIGHLIGHTED_GROUP_POSTFIX : ""));
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        Expression a = operands.get(0);
        if (element.getTextRange().getStartOffset() < a.getTextRange().getStartOffset()) {
            TextRange range = TextRange.create(
                    element.getTextRange().getStartOffset(),
                    a.getTextRange().getStartOffset() -
                            (a.isLeftOverflow() ? 1 : 0)
            );
            if (!range.isEmpty()) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        range, group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                });
            }
        }
        if (a.supportsFoldRegions(document, this)) {
            if (a.isOverflow()) {
                Collections.addAll(descriptors, a.buildFoldRegions(a.getElement(), document, this,
                        group, "", ""));
            } else {
                Collections.addAll(descriptors, a.buildFoldRegions(a.getElement(), document, this));
            }
        }
        if (operands.size() > 1) {
            for (int i = 0; i < operands.size() - 1; i++) {
                Expression b = operands.get(i);
                Expression c = operands.get(i + 1);
                if (c.supportsFoldRegions(document, this)) {
                    if (c.isOverflow()) {
                        Collections.addAll(descriptors, c.buildFoldRegions(c.getElement(), document, this,
                                group, "", ""));
                    } else {
                        Collections.addAll(descriptors, c.buildFoldRegions(c.getElement(), document, this));
                    }
                }
                int finalI = i;
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(
                                b.getTextRange().getEndOffset() +
                                        (b.isRightOverflow() ? 1 : 0),
                                c.getTextRange().getStartOffset() -
                                        (c.isLeftOverflow() ? 1 : 0)
                        ), group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return finalI == 0 ? " += " : " + ";
                    }
                });
            }
        }
        Expression d = operands.get(operands.size() - 1);
        if (d.getTextRange().getEndOffset() < element.getTextRange().getEndOffset()) {
            TextRange range = TextRange.create(
                    d.getTextRange().getEndOffset() +
                            (d.isRightOverflow() ? 1 : 0),
                    element.getTextRange().getEndOffset()
            );
            if (!range.isEmpty()) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        range, group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                });
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isHighlighted() {
        return operands.size() == 1;
    }
}
