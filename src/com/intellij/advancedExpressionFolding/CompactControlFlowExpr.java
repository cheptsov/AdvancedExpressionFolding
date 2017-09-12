package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CompactControlFlowExpr extends Expr implements CompactControlFlow {
    public CompactControlFlowExpr(@NotNull PsiElement element,
                                  @NotNull TextRange textRange) {
        super(element, textRange);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expr parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expr parent) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        buildFoldRegions(element, FoldingGroup.newGroup(CompactControlFlowExpr.class.getName()), descriptors, textRange);
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    public static void buildFoldRegions(@NotNull PsiElement element, FoldingGroup group,
                                        ArrayList<FoldingDescriptor> descriptors, TextRange textRange) {
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getStartOffset(),
                        textRange.getStartOffset() + 1), group) {
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
        descriptors.add(new FoldingDescriptor(element.getNode(),
                TextRange.create(textRange.getEndOffset() - 1,
                        textRange.getEndOffset()), group) {
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });
    }
}
