package com.intellij.bigdecimal;

public class BigDecimalFoldingOptionsProvider extends com.intellij.openapi.options.BeanConfigurable<BigDecimalFoldingSettings.State> implements com.intellij.application.options.editor.CodeFoldingOptionsProvider {
    protected BigDecimalFoldingOptionsProvider() {
        super(BigDecimalFoldingSettings.getInstance().getState());
        BigDecimalFoldingSettings settings = BigDecimalFoldingSettings.getInstance();
        checkBox("Arithmetic expressions", settings::isCollapseOperations, value -> settings.getState().COLLAPSE_OPERATIONS = value);
    }
}
