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
import java.util.Arrays;

public class ForStatement extends Range {
    public static final String FOR_SEPARATOR = ":";
    private final PsiForStatement element;

    public ForStatement(PsiForStatement element, TextRange textRange,
                        Expr operand, Expr startRange, boolean startInclusive,
                        Expr endRange, boolean endInclusive) {
        super(element, textRange, operand, startRange, startInclusive, endRange, endInclusive);
        this.element = element;
        this.separator = FOR_SEPARATOR;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expr parent) {
        // TODO: Refactor this mess
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>(Arrays.asList(super.buildFoldRegions(element, document, parent)));
        if (AdvancedExpressionFoldingSettings.getInstance().getState().isCompactControlFlowSyntaxCollapse()
                && this.element.getLParenth() != null && this.element.getRParenth() != null) {
            // TODO: Refactor this mess
            CompactControlFlowExpr.buildFoldRegions(element,
                    descriptors.size() > 0 ? descriptors.get(0).getGroup() :
                            FoldingGroup.newGroup(CompactControlFlowExpr.class.getName()),
                    descriptors,
                    TextRange.create(this.element.getLParenth().getTextRange().getStartOffset(),
                            this.element.getRParenth().getTextRange().getEndOffset()));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
