package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ControlFlowCodeBlock extends Expression implements ControlFlowBraces {
    @NotNull
    private final PsiCodeBlock element;

    public ControlFlowCodeBlock(@NotNull PsiCodeBlock element, @NotNull TextRange textRange) {
        super(element, textRange);
        this.element = element;
    }

    @Override
    public boolean isNested() {
        return true;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expression parent) {
        return element.getLBrace() != null
                && element.getRBrace() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(ControlFlowCodeBlock.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        if (this.element.getLBrace() != null) {
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    this.element.getLBrace().getTextRange(), group) {
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }
        if (this.element.getRBrace() != null) {
            PsiElement siblingElse = PsiTreeUtil.nextLeaf(this.element.getRBrace(), true);
            if (siblingElse instanceof PsiWhiteSpace) {
                siblingElse = PsiTreeUtil.nextLeaf(siblingElse, true);
            }
            if ((!(siblingElse instanceof PsiKeyword)) || (((PsiKeyword) siblingElse).getTokenType() != JavaTokenType.ELSE_KEYWORD
                    && ((PsiKeyword) siblingElse).getTokenType() != JavaTokenType.WHILE_KEYWORD)
                    && ((PsiKeyword) siblingElse).getTokenType() != JavaTokenType.CATCH_KEYWORD) {
                siblingElse = null;
            }
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    siblingElse != null ?
                            TextRange.create(this.element.getRBrace().getTextRange().getStartOffset(),
                                    siblingElse.getTextRange().getStartOffset()) : this.element.getRBrace().getTextRange(), group) {
                @Override
                public String getPlaceholderText() {
                    return "";
                }
            });
        }

        return descriptors.toArray(FoldingDescriptor.EMPTY);

    }
}
