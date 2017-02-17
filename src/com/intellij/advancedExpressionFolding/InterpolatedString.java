package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InterpolatedString extends Expression implements ConcatenationExpression {
    private final List<Expression> operands;

    public InterpolatedString(TextRange textRange, List<Expression> operands) {
        super(textRange);
        this.operands = operands;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder("\"");
        for (Expression operand : operands) {
            if (!(operand instanceof StringLiteral)) {
                sb.append("$");
                sb.append(operand.format());
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
                && operands.stream().allMatch(o -> o.supportsFoldRegions(document, quick));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        FoldingGroup group = FoldingGroup.newGroup(InterpolatedString.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        for (int i = 0; i < operands.size(); i++) {
            int fI = i;
            if (i == 0) {
                if ((operands.get(i) instanceof StringLiteral)) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(operands.get(i).getTextRange().getEndOffset() - 1,
                                    operands.get(fI).getTextRange().getEndOffset()),
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(getTextRange().getStartOffset(),
                                    operands.get(fI).getTextRange().getEndOffset()),
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            if (operands.get(fI) instanceof Variable) {
                                return "\"$" + operands.get(fI).format();
                            } else {
                                return "\"${" + operands.get(fI).format() + "}";
                            }
                        }
                    });
                }
            } else if (i == operands.size() - 1) {
                if (operands.get(i) instanceof StringLiteral) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(operands.get(i - 1).getTextRange().getEndOffset(),
                                    operands.get(fI).getTextRange().getStartOffset() + 1),
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                } else {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(operands.get(i - 1).getTextRange().getEndOffset(),
                                    operands.get(fI).getTextRange().getEndOffset()),
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            if (operands.get(fI) instanceof Variable) {
                                return "$" + operands.get(fI).format() + "\"";
                            } else {
                                return "${" + operands.get(fI).format() + "}\"";
                            }
                        }
                    });
                }
            } else {
                if (operands.get(i) instanceof StringLiteral) {
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(operands.get(i - 1).getTextRange().getEndOffset(),
                                    operands.get(fI).getTextRange().getStartOffset() + 1),
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                    descriptors.add(new FoldingDescriptor(element.getNode(),
                            TextRange.create(operands.get(i).getTextRange().getEndOffset() - 1,
                                    operands.get(i).getTextRange().getEndOffset()),
                            group) {
                        @Nullable
                        @Override
                        public String getPlaceholderText() {
                            return "";
                        }
                    });
                } else {
                    if (operands.get(i) instanceof Variable) {
                        descriptors.add(new FoldingDescriptor(element.getNode(),
                                TextRange.create(operands.get(i - 1).getTextRange().getEndOffset(),
                                        operands.get(fI).getTextRange().getStartOffset()),
                                group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "$";
                            }
                        });
                    } else {
                        descriptors.add(new FoldingDescriptor(element.getNode(),
                                TextRange.create(operands.get(i - 1).getTextRange().getEndOffset(),
                                        operands.get(fI).getTextRange().getEndOffset()),
                                group) {
                            @Nullable
                            @Override
                            public String getPlaceholderText() {
                                return "${" + operands.get(fI).format() + "}";
                            }
                        });
                    }
                }

            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}
