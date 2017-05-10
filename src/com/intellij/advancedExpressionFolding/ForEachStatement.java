package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ForEachStatement extends Expression implements RangeExpression {
    private final @NotNull TextRange declarationTextRange;
    private final @NotNull TextRange variableTextRange;
    private final @NotNull TextRange arrayTextRange;

    public ForEachStatement(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull TextRange declarationTextRange,
                            @NotNull TextRange variableTextRange, @NotNull TextRange arrayTextRange) {
        super(element, textRange);
        this.declarationTextRange = declarationTextRange;
        this.variableTextRange = variableTextRange;
        this.arrayTextRange = arrayTextRange;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ForEachStatement.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset(),
                declarationTextRange.getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(variableTextRange.getEndOffset(),
                arrayTextRange.getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return " : ";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(arrayTextRange.getEndOffset(),
                declarationTextRange.getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return ") {\n";
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
