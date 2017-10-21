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

public class IfExpression extends Expression implements CollapseByDefault {
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
    private final PsiIfStatement element;

    public IfExpression(PsiIfStatement element, TextRange textRange) {
        super(element, textRange);
        this.element = element;
    }

    public static boolean isCompactExpression(AdvancedExpressionFoldingSettings.State state, PsiIfStatement element) {
        return state.isCompactControlFlowSyntaxCollapse()
                && element.getRParenth() != null
                && element.getLParenth() != null
                && element.getCondition() != null;
    }

    public static boolean isAssertExpression(AdvancedExpressionFoldingSettings.State state, PsiIfStatement element) {
        // TODO: Find another way to avoid colliding "control flow single statement" and "assert"
        return state.isAssertsCollapse() && !state.isControlFlowSingleStatementCodeBlockCollapse()
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
    public boolean supportsFoldRegions(@NotNull Document document, @Nullable Expression parent) {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        return isAssertExpression(state, element) || isCompactExpression(state, element);
    }

    @Override
    public boolean isNested() {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document,
                                                @Nullable Expression parent) {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        FoldingGroup group = FoldingGroup.newGroup(IfExpression.class.getName()
                + (!isAssertExpression(state, this.element) && isCompactExpression(state, this.element) ? Expression.HIGHLIGHTED_GROUP_POSTFIX : ""));
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        if (this.element.getLParenth() != null && this.element.getRParenth() != null) {
            if (isAssertExpression(state, this.element)) {
                @Nullable PsiThrowStatement throwStatement =
                        this.element.getThenBranch() instanceof PsiBlockStatement
                                &&
                                ((PsiBlockStatement) this.element.getThenBranch()).getCodeBlock().getStatements().length ==
                                        1
                                ? ((PsiThrowStatement) ((PsiBlockStatement) this.element.getThenBranch()).getCodeBlock()
                                .getStatements()[0]) : ((PsiThrowStatement) this.element.getThenBranch());
                if (this.element.getCondition() != null && throwStatement != null) {
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
                    @Nullable PsiNewExpression newException = throwStatement.getException() instanceof PsiNewExpression
                            ? ((PsiNewExpression) throwStatement.getException())
                            : null;
                    if (newException != null
                            && newException.getArgumentList() != null
                            && newException.getArgumentList().getExpressions().length > 0
                            && newException.getArgumentList().getExpressions()[0] instanceof PsiLiteralExpression
                            && newException.getArgumentList().getExpressions()[0].getType() != null
                            && newException.getArgumentList().getExpressions()[0].getType().getCanonicalText().equals("java.lang.String")) {
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
                                            newException.getArgumentList()
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
                                            newException.getArgumentList()
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
                                    TextRange.create(newException.getArgumentList()
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
                                    TextRange.create(newException.getArgumentList()
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
                                @NotNull
                                @Override
                                public String getPlaceholderText() {
                                    return state.isSemicolonsCollapse() ? "" : ";";
                                }
                            });
                        }
                    }
                }
            } else if (isCompactExpression(state, this.element)) {
                CompactControlFlowExpression.buildFoldRegions(element, group, descriptors,
                        TextRange.create(this.element.getLParenth().getTextRange().getStartOffset(),
                                this.element.getRParenth().getTextRange().getEndOffset()));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isHighlighted() {
        AdvancedExpressionFoldingSettings.State state = AdvancedExpressionFoldingSettings.getInstance().getState();
        return !isAssertExpression(state, this.element) && isCompactExpression(state, this.element);
    }

    @Override
    public TextRange getHighlightedTextRange() {
        if (this.element.getLParenth() != null && this.element.getRParenth() != null) {
            return TextRange.create(this.element.getLParenth().getTextRange().getStartOffset(),
                    this.element.getRParenth().getTextRange().getEndOffset());
        } else {
            return super.getHighlightedTextRange();
        }
    }
}
