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

public class Pow extends Function implements ArithmeticExpression {
    public Pow(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expression> operands) {
        super(element, textRange, "pow", operands);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return operands.get(0).getTextRange().getEndOffset() < getTextRange().getEndOffset()
                && superscript(operands.get(1).getElement().getText()) != null; // TODO no-format: Forbid non-literal/non-variable operands.get(1)
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(Pow.class.getName());
        if (getTextRange().getStartOffset() < operands.get(0).getTextRange().getStartOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(getTextRange().getStartOffset(),
                            operands.get(0).getTextRange().getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return operands.get(0) instanceof Operation
                            ? "(" : "";
                }
            });
        }
        if (operands.get(0).supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, operands.get(0).buildFoldRegions(operands.get(0).getElement(), document, this));
        }
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(operands.get(0).getTextRange().getEndOffset(),
                        getTextRange().getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                String b = operands.get(1).getElement().getText();
                return operands.get(0) instanceof Operation
                        ? ")" + superscript(b) : superscript(b);
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
