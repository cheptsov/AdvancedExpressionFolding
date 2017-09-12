package com.intellij.advancedExpressionFolding;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.IndentHelper;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class AbstractControlFlowCodeBlock extends Expr {
    @NotNull
    private final PsiCodeBlock element;
    private IndentHelper indentHelper;

    public AbstractControlFlowCodeBlock(@NotNull PsiCodeBlock element, @NotNull TextRange textRange) {
        super(element, textRange);
        this.element = element;
        this.indentHelper = IndentHelper.getInstance();
    }

    @Override
    public boolean isNested() {
        return true;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expr parent) {
        return element.getLBrace() != null
                && element.getRBrace() != null;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expr parent) {
        FoldingGroup group = FoldingGroup.newGroup(AbstractControlFlowCodeBlock.class.getName());
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
            boolean smart = false;
            if (element.getParent() != null) {
                PsiElement thisStatement;
                if (element.getParent().getParent() instanceof PsiIfStatement ||
                        element.getParent().getParent() instanceof PsiLoopStatement) {
                    thisStatement = element.getParent().getParent();
                } else if (element.getParent() instanceof PsiSwitchStatement ||
                        element.getParent() instanceof PsiTryStatement ||
                        element.getParent() instanceof PsiCatchSection) {
                    thisStatement = element.getParent();
                } else {
                    thisStatement = null;
                }
                if (thisStatement != null) {
                    int thisStatementIndent = indentHelper.getIndent(element.getProject(), JavaFileType.INSTANCE, thisStatement.getNode());
                    PsiElement before = PsiTreeUtil.prevLeaf(this.element.getRBrace(), true);
                    PsiElement after = PsiTreeUtil.prevLeaf(this.element.getRBrace(), true);
                    if (before instanceof PsiWhiteSpace && after instanceof PsiWhiteSpace) {
                        smart = true;
                        int startOffset = this.element.getRBrace().getTextRange().getStartOffset();
                        boolean newLine = false;
                        int endOffset = this.element.getRBrace().getTextRange().getEndOffset();
                        while (endOffset < document.getTextLength()) {
                            endOffset++;
                            char c = document.getText(TextRange.create(endOffset - 1, endOffset)).charAt(0);
                            if (c != ' ' && c != '\t') {
                                if (c != '\n') {
                                    endOffset--;
                                } else {
                                    newLine = true;
                                }
                                break;
                            }
                        }
                        if (newLine) {
                            for (int i = 0; i < thisStatementIndent/* - parentStatementIndent*/; i++) {
                                char c = document.getText(TextRange.create(startOffset - 1, startOffset)).charAt(0);
                                if (c != ' ' && c != '\t') {
                                    smart = false;
                                    break;
                                }
                                startOffset--;
                            }
                        }
                        if (smart) {
                            descriptors.add(new FoldingDescriptor(element.getNode(),
                                    TextRange.create(startOffset, endOffset), group) {
                                @Override
                                public String getPlaceholderText() {
                                    return "";
                                }
                            });
                        }
                    }
                }
            }
            if (!smart) {
                PsiElement siblingKeyword = PsiTreeUtil.nextLeaf(this.element.getRBrace(), true);
                if (siblingKeyword instanceof PsiWhiteSpace) {
                    siblingKeyword = PsiTreeUtil.nextLeaf(siblingKeyword, true);
                }
                if (!(siblingKeyword instanceof PsiKeyword)) {
                    siblingKeyword = null;
                } else {
                    PsiKeyword keyword = (PsiKeyword) siblingKeyword;
                    if (keyword.getTokenType() != JavaTokenType.ELSE_KEYWORD
                            && keyword.getTokenType() != JavaTokenType.WHILE_KEYWORD
                            && keyword.getTokenType() != JavaTokenType.CATCH_KEYWORD
                            && keyword.getTokenType() != JavaTokenType.FINALLY_KEYWORD) {
                        siblingKeyword = null;
                    }
                }
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        siblingKeyword != null ?
                                TextRange.create(this.element.getRBrace().getTextRange().getStartOffset(),
                                        siblingKeyword.getTextRange().getStartOffset()) : this.element.getRBrace()
                                .getTextRange(), group) {
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                });
            }
        }

        return descriptors.toArray(FoldingDescriptor.EMPTY);

    }
}
