package com.intellij.advancedExpressionFolding;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "AdvancedExpressionFoldingSettings", storages = @Storage("editor.codeinsight.xml"))
public class AdvancedExpressionFoldingSettings implements PersistentStateComponent<AdvancedExpressionFoldingSettings.State> {
    private final State myState = new State();

    @NotNull
    @Override
    public State getState() {
        return myState;
    }

    public boolean isArithmeticExpressionsCollapse() {
        return myState.ARITHMETIC_EXPRESSIONS;
    }

    public boolean isConcatenationExpressionsCollapse() {
        return myState.CONCATENATION_EXPRESSIONS;
    }

    public boolean isSlicingExpressionsCollapse() {
        return myState.SLICING_EXPRESSIONS;
    }

    public boolean isComparingExpressionsCollapse() {
        return myState.COMPARING_EXPRESSIONS;
    }

    @NotNull
    public static AdvancedExpressionFoldingSettings getInstance() {
        return ServiceManager.getService(AdvancedExpressionFoldingSettings.class);
    }     
    
    @Override
    public void loadState(State state) {
        myState.ARITHMETIC_EXPRESSIONS = state.ARITHMETIC_EXPRESSIONS;
        myState.CONCATENATION_EXPRESSIONS = state.CONCATENATION_EXPRESSIONS;
        myState.SLICING_EXPRESSIONS = state.SLICING_EXPRESSIONS;
        myState.COMPARING_EXPRESSIONS = state.COMPARING_EXPRESSIONS;
    }

    public static final class State {
        public boolean ARITHMETIC_EXPRESSIONS = true;
        public boolean CONCATENATION_EXPRESSIONS = true;
        public boolean SLICING_EXPRESSIONS = true;
        public boolean COMPARING_EXPRESSIONS = true;

        public State() { /* compiled code */ }
    }
}
