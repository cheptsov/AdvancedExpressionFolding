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
    public Pow(TextRange textRange, List<Expression> operands) {
        super(textRange, "pow", operands);
    }

    @Override
    protected Pow copy(List<Expression> newOperands) {
        return new Pow(textRange, newOperands);
    }

    @Override
    public String format() {
        String a = operands.get(0).format();
        String b = operands.get(1).format();
        String bs = superscript(b);
        // TODO: Extend "format" with "superscript" parameter
        /*if (bs != null && !bs.contains(getCharacter())) {*/
            return a + bs;
        /*} else {
            return a + " " + getCharacter() + " " + b;
        }*/
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null && operands.get(0).getTextRange() != null
                && operands.get(0).getTextRange().getEndOffset() < getTextRange().getEndOffset();
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(Abs.class.getName());
        if (getTextRange().getStartOffset() < operands.get(0).getTextRange().getStartOffset()) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(getTextRange().getStartOffset(),
                            operands.get(0).getTextRange().getStartOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return operands.get(0) instanceof Operation
                            ? "(" : "";
                }
            });
        }
        if (operands.get(0).supportsFoldRegions(document, false)) {
            Collections.addAll(descriptors, operands.get(0).buildFoldRegions(element, document));
        }
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(operands.get(0).getTextRange().getEndOffset(),
                        getTextRange().getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                String b = operands.get(1).format();
                return operands.get(0) instanceof Operation
                        ? ")" + superscript(b) : superscript(b);
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
