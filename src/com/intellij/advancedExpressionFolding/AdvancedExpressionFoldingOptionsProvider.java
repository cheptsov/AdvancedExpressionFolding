package com.intellij.advancedExpressionFolding;

public class AdvancedExpressionFoldingOptionsProvider extends com.intellij.openapi.options.BeanConfigurable<AdvancedExpressionFoldingSettings.State> implements com.intellij.application.options.editor.CodeFoldingOptionsProvider {
    protected AdvancedExpressionFoldingOptionsProvider() {
        super(AdvancedExpressionFoldingSettings.getInstance().getState());
        AdvancedExpressionFoldingSettings settings = AdvancedExpressionFoldingSettings.getInstance();
        checkBox("Arithmetic expressions", settings::isArithmeticExpressionsCollapse, value -> settings.getState().ARITHMETIC_EXPRESSIONS = value);
        checkBox("Concatenation expressions", settings::isConcatenationExpressionsCollapse, value -> settings.getState().CONCATENATION_EXPRESSIONS = value);
        checkBox("Slicing expressions", settings::isSlicingExpressionsCollapse, value -> settings.getState().SLICING_EXPRESSIONS = value);
        checkBox("Comparing expressions", settings::isComparingExpressionsCollapse, value -> settings.getState().COMPARING_EXPRESSIONS = value);
    }
}
