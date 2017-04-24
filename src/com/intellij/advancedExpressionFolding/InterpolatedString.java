package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InterpolatedString extends Expression implements ConcatenationExpression {
    private final List<Expression> operands;

    public InterpolatedString(PsiElement element, TextRange textRange, List<Expression> operands) {
        super(element, textRange);
        this.operands = operands;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder("\"");
        for (Expression operand : operands) {
            if (!(operand instanceof StringLiteral)) {
                sb.append("$");
                if (!(operand instanceof Variable)) {
                    sb.append("{");
                }
                sb.append(operand.format());
                if (!(operand instanceof Variable)) {
                    sb.append("}");
                }
            } else {
                String formatted = operand.format();
                sb.append(formatted.substring(1, formatted.length() - 1));
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    @Override
    public boolean supportsFoldRegions(Document document, boolean quick) {
        return getTextRange() != null
                && operands.stream().allMatch(o -> o.getTextRange() != null);
    }

    protected static Set<String> supportedTokens = new HashSet<String>() {
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
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(InterpolatedString.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        final String[] buf = {""};
        if (!(operands.get(0) instanceof StringLiteral)) {
            TextRange range = TextRange.create(operands.get(0).getTextRange().getStartOffset() - 1,
                    operands.get(0).getTextRange().getStartOffset());
            String token = document.getText(range);
            if (supportedTokens.contains(token)) {
                if (operands.get(0) instanceof Variable) {
                    descriptors.add(new FoldingDescriptor(element.getNode(), range, group) {
                        @Override
                        public String getPlaceholderText() {
                            return token + "\"$";
                        }
                    });
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(), range, group) {
                        @Override
                        public String getPlaceholderText() {
                            return token + "\"${";
                        }
                    });
                    buf[0] = "}";
                }
            } else {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(operands.get(0).getTextRange().getStartOffset(),
                                operands.get(0).getTextRange().getEndOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        if (operands.get(0) instanceof Variable) {
                            return "\"$" + operands.get(0).format();
                        } else {
                            return "\"${" + operands.get(0).format() + "}";
                        }
                    }
                });
            }
        }
        for (int i = 0; i < operands.size() - 1; i++) {
            int s = operands.get(i) instanceof StringLiteral
                    ? operands.get(i).getTextRange().getEndOffset() - 1
                    : operands.get(i).getTextRange().getEndOffset();
            int e = operands.get(i + 1) instanceof StringLiteral
                    ? operands.get(i + 1).getTextRange().getStartOffset() + 1
                    : operands.get(i + 1).getTextRange().getStartOffset();
            int fI = i;
            descriptors.add(new FoldingDescriptor(element.getNode(),
                    TextRange.create(s, e), group) {
                @Nullable
                @Override
                public String getPlaceholderText() {
                    StringBuilder sb = new StringBuilder().append(buf[0]);
                    if (!(operands.get(fI + 1) instanceof StringLiteral)) {
                        sb.append("$");
                    }
                    if (!(operands.get(fI + 1) instanceof Variable) && !(operands.get(fI + 1) instanceof StringLiteral)) {
                        sb.append("{");
                        buf[0] = "}";
                    } else {
                        buf[0] = "";
                    }
                    return sb.toString();
                }
            });
        }
        if (!(operands.get(operands.size() - 1) instanceof StringLiteral)
                && document.getTextLength() > operands.get(operands.size() - 1).getTextRange().getEndOffset() + 1) {
            TextRange range = TextRange.create(operands.get(operands.size() - 1).getTextRange().getEndOffset(),
                    operands.get(operands.size() - 1).getTextRange().getEndOffset() + 1);
            int s = operands.get(operands.size() - 2) instanceof StringLiteral
                    ? operands.get(operands.size() - 2).getTextRange().getEndOffset() - 1
                    : operands.get(operands.size() - 2).getTextRange().getEndOffset();
            int e = operands.get(operands.size() - 1) instanceof StringLiteral
                    ? operands.get(operands.size() - 1).getTextRange().getStartOffset() + 1
                    : operands.get(operands.size() - 1).getTextRange().getStartOffset();
            String token = document.getText(range);
            if (supportedTokens.contains(token)) {
                if (operands.get(operands.size() - 1) instanceof Variable) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(s, e), group) {
                        @Override
                        public String getPlaceholderText() {
                            return "$";
                        }
                    });
                    descriptors.add(new FoldingDescriptor(element.getNode(), range, group) {
                        @Override
                        public String getPlaceholderText() {
                            return "\"" + token;
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
                    descriptors.add(new FoldingDescriptor(element.getNode(), range, group) {
                        @Override
                        public String getPlaceholderText() {
                            return "}\"" + token;
                        }
                    });
                }
            } else {
                descriptors.add(new FoldingDescriptor(element.getNode(),
                        TextRange.create(operands.get(operands.size() - 1).getTextRange().getStartOffset(),
                                operands.get(operands.size() - 1).getTextRange().getEndOffset()), group) {
                    @Nullable
                    @Override
                    public String getPlaceholderText() {
                        return operands.get(operands.size() - 1).format() + buf[0] + "\"";
                    }
                });
            }
        }
        for (Expression operand : operands) {
            if (operand.supportsFoldRegions(document, false)) {
                Collections.addAll(descriptors, operand.buildFoldRegions(operand.getElement(), document));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
