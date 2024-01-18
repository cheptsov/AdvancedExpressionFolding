package com.intellij.advancedExpressionFolding;

public class AdvancedExpressionFoldingOptionsProvider extends com.intellij.openapi.options.BeanConfigurable<AdvancedExpressionFoldingSettings.State> implements com.intellij.application.options.editor.CodeFoldingOptionsProvider {
    protected AdvancedExpressionFoldingOptionsProvider() {
        super(AdvancedExpressionFoldingSettings.getInstance().getState());
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        AdvancedExpressionFoldingSettings.State state = settings.getState();

        checkBox("arithmeticExpressionsCollapse", "Math, BigDecimal and BigInteger expressions (deprecated)");
        checkBox("concatenationExpressionsCollapse", "StringBuilder.append and Collection.add/remove expressions, interpolated Strings and Stream expressions");
        checkBox("slicingExpressionsCollapse", "List.subList and String.substring expressions");
        checkBox("comparingExpressionsCollapse", "Object.equals and Comparable.compareTo expressions");
        checkBox("java.time isBefore/isAfter expressions", state::isComparingLocalDatesCollapse, state::setComparingLocalDatesCollapse);
        checkBox("LocalDate.of literals (e.g. 2018-02-12)" , state::isLocalDateLiteralCollapse, state::setLocalDateLiteralCollapse);
        checkBox("Postfix LocalDate literals (e.g. 2018Y-02M-12D) " , state::isLocalDateLiteralPostfix, state::setLocalDateLiteralPostfix);
        checkBox("getExpressionsCollapse", "List.get, List.set, Map.get and Map.put expressions, array and list literals");
        checkBox("rangeExpressionsCollapse", "For loops, range expressions");
        checkBox("checkExpressionsCollapse", "Null safe calls");
        checkBox("castExpressionsCollapse", "Type cast expressions");
        checkBox("varExpressionsCollapse", "Variable declarations");
        checkBox("getSetExpressionsCollapse", "Getters and setters");
        checkBox("controlFlowSingleStatementCodeBlockCollapse", "Control flow single-statement code block braces (read-only files)");
        checkBox("controlFlowMultiStatementCodeBlockCollapse", "Control flow multi-statement code block braces (read-only files, deprecated)");
        checkBox("compactControlFlowSyntaxCollapse", "Compact control flow condition syntax");
        checkBox("semicolonsCollapse", "Semicolons (read-only files)");
        checkBox("assertsCollapse", "Asserts");
    }
}
