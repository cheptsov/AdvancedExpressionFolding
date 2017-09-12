package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Exp extends Function implements ArithmeticExpression {
    public Exp(PsiElement element, TextRange textRange, List<Expr> operands) {
        super(element, textRange, "exp", operands);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expr parent) {
        return superscript(operands.get(0).getElement().getText()) != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expr parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(Exp.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "\uD835\uDC52" + superscript(operands.get(0).getElement().getText());
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
