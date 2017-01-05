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

public class Abs extends Function implements ArithmeticExpression {
    public Abs(TextRange textRange, List<Expression> operands) {
        super(textRange, "abs", operands);
    }

    @Override
    protected Abs copy(List<Expression> newOperands) {
        return new Abs(textRange, newOperands);
    }

    @Override
    public String format() {
        return "|" + operands.get(0).format() + "|";
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && operands.get(0).getTextRange() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(Abs.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(getTextRange().getStartOffset(),
                        operands.get(0).getTextRange().getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "|";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(operands.get(0).getTextRange().getEndOffset(),
                        getTextRange().getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "|";
            }
        });
        if (operands.get(0).supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, operands.get(0).buildFoldRegions(element, document));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
