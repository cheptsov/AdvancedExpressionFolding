package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IfExpr extends Expr implements CollapseByDefault {
    private final PsiIfStatement element;

    private static final Set<String> supportedOperatorSigns = new HashSet<String>() {
        {
            add("==");
            add("!=");
            add(">");
            add("<");
            add(">=");
            add("<=");
        }
    };

    public IfExpr(PsiIfStatement element, TextRange textRange) {
        super(element, textRange);
        this.element = element;
    }

    public static boolean isCompactExpression(AdvancedExpressionFoldingSettings.State state, PsiIfStatement element) {
        return state.isCompactControlFlowSyntaxCollapse()
                && element.getRParenth() != null
                && element.getLParenth() != null;
    }

    public static boolean isAssertExpression(AdvancedExpressionFoldingSettings.State state, PsiIfStatement element) {
        return state.isAssertsCollapse()
                && element.getCondition() instanceof PsiBinaryExpression
                && supportedOperatorSigns.contains(((PsiBinaryExpression) element.getCondition()).getOperationSign().getText())
                && element.getElseBranch() == null
                && (element.getThenBranch() instanceof PsiBlockStatement
                && ((PsiBlockStatement) element.getThenBranch()).getCodeBlock().getStatements().length == 1
                && ((PsiBlockStatement) element.getThenBranch()).getCodeBlock()
                .getStatements()[0] instanceof PsiThrowStatement
                || element.getThenBranch() instanceof PsiThrowStatement);
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expr parent) {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        return isAssertExpression(state, element) || isCompactExpression(state, element);
    }

    @Override
    public boolean isNested() {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expr parent) {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        FoldingGroup group = FoldingGroup.newGroup(IfExpr.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        if (isAssertExpression(state, this.element)) {
            PsiThrowStatement throwStatement =
                    this.element.getThenBranch() instanceof PsiBlockStatement
                            &&
                            ((PsiBlockStatement) this.element.getThenBranch()).getCodeBlock().getStatements().length ==
                                    1
                            ? ((PsiThrowStatement) ((PsiBlockStatement) this.element.getThenBranch()).getCodeBlock()
                            .getStatements()[0]) : ((PsiThrowStatement) this.element.getThenBranch());
            boolean trailingSpace = document.getText(TextRange.create(
                    this.element.getLParenth().getTextRange().getStartOffset() - 1,
                    this.element.getLParenth().getTextRange().getStartOffset()
            )).equals(" ");
            if (trailingSpace) {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(this.element.getTextRange().getStartOffset(),
                                this.element.getLParenth().getTextRange().getStartOffset() - 1), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "assert";
                    }
                });
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(this.element.getLParenth().getTextRange().getStartOffset(),
                                this.element.getCondition().getTextRange().getStartOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "";
                    }
                });
            } else {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(this.element.getTextRange().getStartOffset(),
                                this.element.getCondition().getTextRange().getStartOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return "assert ";
                    }
                });
            }
            PsiBinaryExpression binaryExpression = ((PsiBinaryExpression) this.element.getCondition());
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    binaryExpression.getOperationSign().getTextRange(), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    switch (binaryExpression.getOperationSign().getText()) {
                        case "==":
                            return "!=";
                        case "!=":
                            return "==";
                        case ">":
                            return "<=";
                        case "<":
                            return ">=";
                        case ">=":
                            return "<";
                        case "<=":
                            return ">";
                        default:
                            throw new IllegalStateException("Unsupported operator: " + binaryExpression.getOperationSign().getText());
                    }
                }
            });
            if (throwStatement.getException() instanceof PsiNewExpression &&
                    ((PsiNewExpression) throwStatement.getException()).getArgumentList().getExpressions().length > 0
                    && ((PsiNewExpression) throwStatement.getException()).getArgumentList()
                    .getExpressions()[0] instanceof PsiLiteralExpression
                    && ((PsiNewExpression) throwStatement.getException()).getArgumentList()
                    .getExpressions()[0].getType().getCanonicalText().equals("java.lang.String")) {
                boolean spacesAroundColon = document.getText(TextRange.create(
                        throwStatement.getTextRange().getStartOffset() - 3,
                        throwStatement.getTextRange().getStartOffset()
                )).equals("   ");
                if (spacesAroundColon) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(this.element.getRParenth().getTextRange().getEndOffset() - 1,
                                    throwStatement.getTextRange().getStartOffset() - 3), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(throwStatement.getTextRange().getStartOffset() - 2,
                                    throwStatement.getTextRange().getStartOffset() - 1), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return ":";
                        }
                    });
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(throwStatement.getTextRange().getStartOffset(),
                                    ((PsiNewExpression) throwStatement.getException()).getArgumentList()
                                            .getExpressions()[0]
                                            .getTextRange().getStartOffset()), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(this.element.getCondition().getTextRange().getEndOffset(),
                                    ((PsiNewExpression) throwStatement.getException()).getArgumentList()
                                            .getExpressions()[0]
                                            .getTextRange().getStartOffset()), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return " : ";
                        }
                    });
                }
                if (!state.isSemicolonsCollapse() && throwStatement.getText().endsWith(";")) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(((PsiNewExpression) throwStatement.getException()).getArgumentList()
                                            .getExpressions()[0].getTextRange().getEndOffset(),
                                    throwStatement.getTextRange().getEndOffset() - 1), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                    if (this.element.getTextRange().getEndOffset() > throwStatement.getTextRange().getEndOffset()) {
                        descriptors.add(new FoldingDescriptor(element.getNode(),
                                TextRange.create(throwStatement.getTextRange().getEndOffset(),
                                        this.element.getTextRange().getEndOffset()), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "";
                            }
                        });
                    }
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(((PsiNewExpression) throwStatement.getException()).getArgumentList()
                                            .getExpressions()[0].getTextRange().getEndOffset(),
                                    this.element.getTextRange().getEndOffset()), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return state.isSemicolonsCollapse() ? "" : ";";
                        }
                    });
                }
            } else {
                if (!state.isSemicolonsCollapse() && throwStatement.getText().endsWith(";")) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(this.element.getCondition().getTextRange().getEndOffset(),
                                    throwStatement.getTextRange().getEndOffset() - 1), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                    if (this.element.getTextRange().getEndOffset() > throwStatement.getTextRange().getEndOffset()) {
                        descriptors.add(new FoldingDescriptor(element.getNode(),
                                TextRange.create(throwStatement.getTextRange().getEndOffset(),
                                        this.element.getTextRange().getEndOffset()), group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "";
                            }
                        });
                    }
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(this.element.getCondition().getTextRange().getEndOffset(),
                                    this.element.getTextRange().getEndOffset()), group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return state.isSemicolonsCollapse() ? "" : ";";
                        }
                    });
                }
            }
        } else if (isCompactExpression(state, this.element)) {
            CompactControlFlowExpr.buildFoldRegions(element, group, descriptors,
                    TextRange.create(this.element.getLParenth().getTextRange().getStartOffset(),
                            this.element.getRParenth().getTextRange().getEndOffset()));
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
