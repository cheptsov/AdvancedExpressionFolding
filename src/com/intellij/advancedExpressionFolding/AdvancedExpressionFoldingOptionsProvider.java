package com.intellij.advancedExpressionFolding;

public class AdvancedExpressionFoldingOptionsProvider extends com.intellij.openapi.options.BeanConfigurable<AdvancedExpressionFoldingSettings.State> implements com.intellij.application.options.editor.CodeFoldingOptionsProvider {
    protected AdvancedExpressionFoldingOptionsProvider() {
        super(AdvancedExpressionFoldingSettings.getInstance().getState());
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        checkBox("arithmeticExpressionsCollapse", "Math, BigDecimal and BigInteger expressions");
        checkBox("concatenationExpressionsCollapse", "StringBuilder.append and Collection.add/remove expressions, interpolated Strings and Stream expressions");
        checkBox("slicingExpressionsCollapse", "List.subList and String.substring expressions");
        checkBox("comparingExpressionsCollapse", "Object.equals and Comparable.compareTo expressions");
        checkBox("getExpressionsCollapse", "List.get, List.set, Map.get and Map.put expressions, array and list literals");
        checkBox("rangeExpressionsCollapse", "For loops, range expressions");
        checkBox("checkExpressionsCollapse", "Null safe calls");
        checkBox("castExpressionsCollapse", "Type cast expressions");
        checkBox("varExpressionsCollapse", "Variable declarations");
        checkBox("getSetExpressionsCollapse", "Getters and setters");
    }
}
