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

public class Cbrt extends Function implements ArithmeticExpression {
    public Cbrt(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange, "∛", operands);
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(Cbrt.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(getTextRange().getStartOffset(),
                        operands.get(0).getTextRange().getStartOffset()), group, operands.get(0) instanceof NumberLiteral ||
                operands.get(0) instanceof Variable ?
                "∛" : "∛("));
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(operands.get(0).getTextRange().getEndOffset(),
                        getTextRange().getEndOffset()), group, operands.get(0) instanceof NumberLiteral ||
                operands.get(0) instanceof Variable ?
                "" : ")"));
        if (operands.get(0).supportsFoldRegions(document, this)) {
            Collections.addAll(descriptors, operands.get(0).buildFoldRegions(operands.get(0).getElement(), document, this));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
