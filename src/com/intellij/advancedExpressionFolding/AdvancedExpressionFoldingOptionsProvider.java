package com.intellij.advancedExpressionFolding;

public class AdvancedExpressionFoldingOptionsProvider extends com.intellij.openapi.options.BeanConfigurable<AdvancedExpressionFoldingSettings.State> implements com.intellij.application.options.editor.CodeFoldingOptionsProvider {
    protected AdvancedExpressionFoldingOptionsProvider() {
        super(AdvancedExpressionFoldingSettings.getInstance().getState());
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        checkBox("Math, BigDecimal and BigInteger expressions", settings::isArithmeticExpressionsCollapse, value -> settings.getState().ARITHMETIC_EXPRESSIONS = value);
        checkBox("StringBuilder.append expressions", settings::isConcatenationExpressionsCollapse, value -> settings.getState().CONCATENATION_EXPRESSIONS = value);
        checkBox("List.subList and String.substring expressions", settings::isSlicingExpressionsCollapse, value -> settings.getState().SLICING_EXPRESSIONS = value);
        checkBox("Object.equals and Comparable.compareTo expressions", settings::isComparingExpressionsCollapse, value -> settings.getState().COMPARING_EXPRESSIONS = value);
        checkBox("List.get, List.set, Map.get and Map.put expressions", settings::isGetExpressionsCollapse, value -> settings.getState().GET_EXPRESSIONS = value);
        checkBox("Range expressions", settings::isRangeExpressionsCollapse, value -> settings.getState().RANGE_EXPRESSIONS = value);
        checkBox("Not-null checks, Set.contains and Map.containsKey expressions", settings::isCheckExpressionsCollapse, value -> settings.getState().CHECK_EXPRESSIONS = value);
        checkBox("Type cast expressions", settings::isCastExpressionsCollapse, value -> settings.getState().CAST_EXPRESSIONS = value);
    }
}
