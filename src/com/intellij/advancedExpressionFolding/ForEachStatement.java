package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ForEachStatement extends Expression implements RangeExpression {
    private final TextRange declarationTextRange;
    private final TextRange variableTextRange;
    private final TextRange arrayTextRange;
    private final String itemName;
    private final String arrayName;

    public ForEachStatement(TextRange textRange, TextRange declarationTextRange,
                            TextRange variableTextRange, TextRange arrayTextRange, String itemName, String arrayName) {
        super(textRange);
        this.declarationTextRange = declarationTextRange;
        this.variableTextRange = variableTextRange;
        this.arrayTextRange = arrayTextRange;
        this.itemName = itemName;
        this.arrayName = arrayName;
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return textRange != null && declarationTextRange != null && variableTextRange != null && arrayTextRange != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ForEachStatement.class.getName());
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset(),
                declarationTextRange.getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(variableTextRange.getEndOffset(),
                arrayTextRange.getStartOffset()), group) {
            @Nullable
            @Override
            public String getPlaceholderText() {
                return " : ";
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
        return "var " + itemName + " : " + arrayName + ")" + "{\n";
    }
}
