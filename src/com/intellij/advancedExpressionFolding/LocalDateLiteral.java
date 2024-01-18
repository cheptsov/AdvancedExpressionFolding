package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalDateLiteral extends Expression {
    public static final String DATE_SEPARATOR = "-";

    public static final String YEAR_POSTFIX = "Y";
    public static final String MONTH_POSTFIX = "M";
    public static final String DAY_POSTFIX = "D";

    @NotNull
    private final PsiLiteralExpression year;
    @NotNull
    private final PsiLiteralExpression month;
    @NotNull
    private final PsiLiteralExpression day;

    public LocalDateLiteral(@NotNull PsiElement element, @NotNull TextRange textRange, @NotNull PsiLiteralExpression year, @NotNull PsiLiteralExpression month, @NotNull PsiLiteralExpression day) {
        super(element, textRange);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public boolean supportsFoldRegions(@NotNull Document document,
                                       @Nullable Expression parent) {
        return true;
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document, @Nullable Expression parent) {
        FoldingGroup group = FoldingGroup.newGroup(ListLiteral.class.getName());
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(textRange.getStartOffset(),
                year.getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return "";
            }
        });

        boolean usePostfix = AdvancedExpressionFoldingSettings.getInstance().getState().isLocalDateLiteralPostfix();

        final String dateSep = DATE_SEPARATOR;
        final String yearPostfix = usePostfix ? YEAR_POSTFIX : "";
        final String monthPostfix = usePostfix ? MONTH_POSTFIX : "";
        final String dayPostfix = usePostfix ? DAY_POSTFIX : "";

        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(year.getTextRange().getEndOffset(),
                month.getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                // Add leading zero to month if month is only a single digit
                if (month.getTextLength() == 1) {
                    return yearPostfix + dateSep + "0";
                } else {
                    return yearPostfix + dateSep;
                }
            }
        });

        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(month.getTextRange().getEndOffset(),
                day.getTextRange().getStartOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                // Add leading zero to day if day is only a single digit
                if (day.getTextLength() == 1) {
                    return monthPostfix + dateSep + "0";
                } else {
                    return monthPostfix + dateSep;
                }
            }
        });

        descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(day.getTextRange().getEndOffset(),
                textRange.getEndOffset()), group) {
            @NotNull
            @Override
            public String getPlaceholderText() {
                return dayPostfix + "";
            }
        });
//        descriptors.add(year.buildFoldRegions(year.getElement(), document, this);
//        descriptors.add(month.buildFoldRegions(month.getElement(), document, this);
//        descriptors.add(day.buildFoldRegions(day.getElement(), document, this);
        return descriptors.toArray(new FoldingDescriptor[0]);
    }}
