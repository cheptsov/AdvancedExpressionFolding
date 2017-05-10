package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ForEachIndexedStatement extends Expression implements RangeExpression {
    private final @NotNull TextRange declarationTextRange;
    private final @NotNull TextRange indexTextRange;
    private final @NotNull TextRange variableTextRange;
    private final @NotNull TextRange arrayTextRange;
    private final boolean varSyntax;
    private final boolean isFinal;

    public ForEachIndexedStatement(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull TextRange declarationTextRange,
                                   @NotNull TextRange indexTextRange,
                                   @NotNull TextRange variableTextRange, @NotNull TextRange arrayTextRange,
                                   boolean varSyntax, boolean isFinal) {
        super(element, textRange);
        this.declarationTextRange = declarationTextRange;
        this.indexTextRange = indexTextRange;
        this.variableTextRange = variableTextRange;
        this.arrayTextRange = arrayTextRange;
        this.varSyntax = varSyntax;
        this.isFinal = isFinal;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, boolean quick) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ForEachIndexedStatement.class.getName());
        TextRange prefixRange = TextRange.create(textRange.getStartOffset(),
                textRange.getStartOffset() + 1);
        String prefix = document.getText(prefixRange);
        if (varSyntax) {
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset(),
                    indexTextRange.getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return prefix + (isFinal ? "val" : "var" ) + " (";
                }
            });
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(indexTextRange.getEndOffset(),
                    variableTextRange.getStartOffset() - 1), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return ",";
                }
            });
        } else {
            descriptors.add(new FoldingDescriptor(element.getNode(), prefixRange, group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return  prefix + "(";
                }
            });
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(indexTextRange.getEndOffset(),
                    declarationTextRange.getStartOffset()), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return ", ";
                }
            });
        }
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(variableTextRange.getEndOffset(),
                arrayTextRange.getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return ") : ";
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
