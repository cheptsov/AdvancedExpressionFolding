package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiForStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ForEachStatement extends Expression {
    @NotNull
    private final PsiForStatement element;
    private final @NotNull TextRange declarationTextRange;
    private final @NotNull TextRange variableTextRange;
    private final @NotNull TextRange arrayTextRange;

    public ForEachStatement(@NotNull PsiForStatement element, @NotNull TextRange textRange, @NotNull TextRange declarationTextRange,
                            @NotNull TextRange variableTextRange, @NotNull TextRange arrayTextRange) {
        super(element, textRange);
        this.element = element;
        this.declarationTextRange = declarationTextRange;
        this.variableTextRange = variableTextRange;
        this.arrayTextRange = arrayTextRange;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        FoldingGroup group = FoldingGroup.newGroup(ForEachStatement.class.getName());
        if (AdvancedExpressionFoldingSettings.getInstance().getState().isCompactControlFlowSyntaxCollapse()
                && this.element.getLParenth() != null) {
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(this.element.getLParenth().getTextRange().getStartOffset(),
                    this.element.getLParenth().getTextRange().getStartOffset() + 1), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
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
                // TODO: Merge the group with ControlFlowMultiStatementCodeBlockExpression & ControlFlowSingleStatementCodeBlockExpression
                // TODO: Hide the curly brace if the setting is enabled
                return AdvancedExpressionFoldingSettings.getInstance().getState().isCompactControlFlowSyntaxCollapse()
                        ? " {\n"
                        : ") {\n";
            }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
