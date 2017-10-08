package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

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
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingDescriptor[] descriptors = super.buildFoldRegions(element, document, this);
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
        if (operands.size() == 1) {
            FoldingGroup group = FoldingGroup.newGroup(Append.class.getName() + Expression.HIGHLIGHTED_GROUP_POSTFIX);
            FoldingDescriptor[] newDescriptors = Arrays.copyOf(descriptors, descriptors.length);
            for (int i = 0; i < newDescriptors.length; i++) {
                FoldingDescriptor d = newDescriptors[i];
                newDescriptors[i] = new FoldingDescriptor(d.getElement(), d.getRange(), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return d.getPlaceholderText();
                    }
                };
            }

            return newDescriptors;
        }
        return descriptors;
    }

    @Override
    public boolean isHighlighted() {
        return operands.size() == 1;
    }
}
