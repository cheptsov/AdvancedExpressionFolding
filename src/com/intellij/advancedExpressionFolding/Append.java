package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Append extends Operation implements ConcatenationExpression {
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
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingDescriptor[] descriptors = super.buildFoldRegions(element, document);
        if (operands.get(0) instanceof Variable && !((Variable)operands.get(0)).isCopy()) {
            FoldingDescriptor[] newDescriptors = Arrays.copyOf(descriptors, descriptors.length);
            for (int i = 0; i < newDescriptors.length; i++) {
                FoldingDescriptor d = newDescriptors[i];
                if (" + ".equals(d.getPlaceholderText())) {
                    newDescriptors[i] = new FoldingDescriptor(d.getElement(), d.getRange(), d.getGroup()) {
                        @NotNull
                        @Override
                        public String getPlaceholderText() {
                            return " += ";
                        }
                    };
                    return newDescriptors;
                }
            }
        }
        return descriptors;
    }

}
