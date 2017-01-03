package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Append extends Operation implements ConcatenationExpression {

    public Append(TextRange textRange, List<Expression> operands) {
        super(textRange, "+", 10, operands);
    }

    @Override
    protected Operation copy(List<Expression> newOperands) {
        return new Append(textRange, newOperands);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }

    @Override
    public String format() {
        String format = super.format();
        if (operands.get(0) instanceof Variable) {
            format = format.replaceFirst("\\+", "+=");
        }
        return format;
    }

    @Override
    public Expression simplify(boolean compute) {
        Append simplified = (Append) super.simplify(compute);
        if (simplified.getOperands().get(0) instanceof StringLiteral && ((StringLiteral) simplified.getOperands().get(0)).getString().equals("")) {
            if (simplified == this) {
                simplified = (Append) copy(new ArrayList<>(simplified.getOperands()));
            }
            simplified.getOperands().remove(0);
            return simplified;
        }
        return simplified;
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
        if (operands.get(0) instanceof Variable) {
            FoldingDescriptor[] newDescriptors = Arrays.copyOf(descriptors, descriptors.length);
            for (int i = 0; i < newDescriptors.length; i++) {
                FoldingDescriptor d = newDescriptors[i];
                if (" + ".equals(d.getPlaceholderText())) {
                    newDescriptors[i] = new FoldingDescriptor(d.getElement(), d.getRange(), d.getGroup()) {
                        @Nullable
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
