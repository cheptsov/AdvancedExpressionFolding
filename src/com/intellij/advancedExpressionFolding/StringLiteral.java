package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class StringLiteral extends Expression implements CharSequenceLiteral {
    private @NotNull String string;

    public StringLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull String string) {
        super(element, textRange);
        this.string = string;
    }

    @NotNull
    public String getString() {
        return string;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return !document.getText(textRange).equals("\"" + string  + "\"");
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(StringLiteral.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(), element.getTextRange(), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "\"" + string  + "\"";
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
