package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InterpolatedString extends Expression {
    private final @NotNull List<Expression> operands;

    public InterpolatedString(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull List<Expression> operands) {
        super(element, textRange);
        this.operands = operands;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    public static Set<String> OVERFLOW_CHARACTERS = new HashSet<String>() {
        {
            add(".");
            add(";");
            add(",");
            add(")");
            add("(");
            add(" ");
        }
    };

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        return buildFoldRegions(element, document, parent, null, null, null);
    }

    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent,
                                                @Nullable FoldingGroup overflowGroup,
                                                @Nullable String overflowLeftPlaceholder,
                                                @Nullable String overflowRightPlaceholder) {
        final Expression first = operands.get(0);
        final Expression last = operands.get(operands.size() - 1);
        FoldingGroup group = overflowGroup != null ? overflowGroup : FoldingGroup.newGroup(InterpolatedString.class.getName() + (isHighlighted() ? Expression.HIGHLIGHTED_GROUP_POSTFIX : ""));
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        final String[] buf = {""};
        if (!(first instanceof CharSequenceLiteral)) {
            TextRange overflowLeftRange = TextRange.create(first.getTextRange().getStartOffset() - 1, first.getTextRange().getStartOffset());
            String overflowLeftText = document.getText(overflowLeftRange);
            if (OVERFLOW_CHARACTERS.contains(overflowLeftText)) {
                final String overflowText = overflowLeftPlaceholder != null ? overflowLeftPlaceholder : overflowLeftText;
                if (first instanceof Variable) {
                    descriptors.add(new FoldingDescriptor(element.getNode(), overflowLeftRange, group) {
                        @Override
                        public String getPlaceholderText() {
                            return overflowText + "\"$";
                        }
                    });
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(), overflowLeftRange, group) {
                        @Override
                        public String getPlaceholderText() {
                            return overflowText + "\"${";
                        }
                    });
                    buf[0] = "}";
                }
            } else {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(first.getTextRange().getStartOffset(),
                                first.getTextRange().getEndOffset()), group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        if (first instanceof Variable) {
                            return "\"$" + first.getElement().getText(); // TODO no-format: not sure
                        } else {
                            return "\"${" + first.getElement().getText() + "}"; // TODO no-format: not sure
                        }
                    }
                });
            }
        } else if (first instanceof CharacterLiteral) {
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(first.getTextRange().getStartOffset(),
                    first.getTextRange().getStartOffset() + 1), group) {
                @Override
                public String getPlaceholderText() {
                    return "\"";
                }
            });
        }
        for (int i = 0; i < operands.size() - 1; i++) {
            int s = operands.get(i) instanceof CharSequenceLiteral
                    ? operands.get(i).getTextRange().getEndOffset() - 1
                    : operands.get(i).getTextRange().getEndOffset();
            int e = operands.get(i + 1) instanceof CharSequenceLiteral
                    ? operands.get(i + 1).getTextRange().getStartOffset() + 1
                    : operands.get(i + 1).getTextRange().getStartOffset();
            int fI = i;
            StringBuilder sI = new StringBuilder().append(buf[0]);
            if (!(operands.get(fI + 1) instanceof CharSequenceLiteral)) {
                sI.append("$");
            }
            if (!(operands.get(fI + 1) instanceof Variable) && !(operands.get(fI + 1) instanceof CharSequenceLiteral)) {
                sI.append("{");
                buf[0] = "}";
            } else {
                buf[0] = "";
            }
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(s, e), group) {
                @NotNull
                @Override
                public String getPlaceholderText() {
                    return sI.toString();
                }
            });
        }
        if (!(last instanceof CharSequenceLiteral)
                && document.getTextLength() > last.getTextRange().getEndOffset() + 1) {
            TextRange overflowRightRange = TextRange.create(last.getTextRange().getEndOffset(), last.getTextRange().getEndOffset() + 1);
            Expression beforeLast = operands.get(operands.size() - 2);
            int s = beforeLast instanceof CharSequenceLiteral
                    ? beforeLast.getTextRange().getEndOffset() - 1
                    : beforeLast.getTextRange().getEndOffset();
            int e = last.getTextRange().getStartOffset();
            String overflowRightText = document.getText(overflowRightRange);
            if (OVERFLOW_CHARACTERS.contains(overflowRightText)) {
                final String overflowText = overflowRightPlaceholder != null ? overflowRightPlaceholder : overflowRightText;
                if (last instanceof Variable) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(s, e), group) {
                        @Override
                        public String getPlaceholderText() {
                            return "$";
                        }
                    });
                    descriptors.add(new FoldingDescriptor(element.getNode(), overflowRightRange, group) {
                        @Override
                        public String getPlaceholderText() {
                            return "\"" + overflowText;
                        }
                    });
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(s, e), group) {
                        @Override
                        public String getPlaceholderText() {
                            return "${";
                        }
                    });
                    descriptors.add(new FoldingDescriptor(element.getNode(), overflowRightRange, group) {
                        @Override
                        public String getPlaceholderText() {
                            return "}\"" + overflowText;
                        }
                    });
                }
            } else {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(last.getTextRange().getStartOffset(),
                                last.getTextRange().getEndOffset()), group) {
                    @NotNull
                    @Override
                    public String getPlaceholderText() {
                        return last.getElement().getText() + buf[0] + "\""; // TODO no-format: not sure
                    }
                });
            }
        } else if (last instanceof CharacterLiteral) {
            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(last.getTextRange().getEndOffset() - 1,
                    last.getTextRange().getEndOffset()), group) {
                @Override
                public String getPlaceholderText() {
                    return "\"";
                }
            });
        }
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, this)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(operand.getElement(), document, this));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Override
    public boolean isRightOverflow() {
        return !(operands.get(operands.size() - 1) instanceof CharSequenceLiteral);
    }

    @Override
    public boolean isHighlighted() {
        return operands.stream().filter(o -> o instanceof CharSequenceLiteral).count() == operands.size()
                && operands.get(0) instanceof StringLiteral
                && operands.get(operands.size() - 1) instanceof StringLiteral;
    }

    @Override
    public boolean isLeftOverflow() {
        return !(operands.get(0) instanceof CharSequenceLiteral);
    }
}
