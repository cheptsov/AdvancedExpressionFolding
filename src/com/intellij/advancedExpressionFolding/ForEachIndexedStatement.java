package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ForEachIndexedStatement extends Expression implements RangeExpression {
    private final TextRange declarationTextRange;
    private final TextRange indexTextRange;
    private final TextRange variableTextRange;
    private final TextRange arrayTextRange;
    private final String indexName;
    private final String itemName;
    private final String arrayName;
    private final boolean varSyntax;
    private final boolean isFinal;

    public ForEachIndexedStatement(TextRange textRange, TextRange declarationTextRange,
                                   TextRange indexTextRange,
                                   TextRange variableTextRange, TextRange arrayTextRange, String indexName,
                                   String itemName, String arrayName, boolean varSyntax, boolean isFinal) {
        super(textRange);
        this.declarationTextRange = declarationTextRange;
        this.indexTextRange = indexTextRange;
        this.variableTextRange = variableTextRange;
        this.arrayTextRange = arrayTextRange;
        this.indexName = indexName;
        this.itemName = itemName;
        this.arrayName = arrayName;
        this.varSyntax = varSyntax;
        this.isFinal = isFinal;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null && declarationTextRange != null && variableTextRange != null && arrayTextRange != null;
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
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return prefix + (isFinal ? "val" : "var" ) + " (";
                }
            });
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(indexTextRange.getEndOffset(),
                    variableTextRange.getStartOffset() - 1), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return ",";
                }
            });
        } else {
            descriptors.add(new FoldingDescriptor(element.getNode(), prefixRange, group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return  prefix + "(";
                }
            });
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(indexTextRange.getEndOffset(),
                    declarationTextRange.getStartOffset()), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    return ", ";
                }
            });
        }
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(variableTextRange.getEndOffset(),
                arrayTextRange.getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return ") : ";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(arrayTextRange.getEndOffset(),
                declarationTextRange.getEndOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return ") {\n";
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public String format() {
        // TODO: Get rid out of format here at least as first "(" might be not correct
        return "(" + (isFinal ? "val" : "var" ) + " (" + itemName + ", "  + indexName + ") : " + arrayName + ")" + "{\n";
    }
}
