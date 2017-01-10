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

    public boolean isGetExpressionsCollapse() {
        return myState.GET_EXPRESSIONS;
    }

    public boolean isRangeExpressionsCollapse() {
        return myState.RANGE_EXPRESSIONS;
    }

    public boolean isCheckExpressionsCollapse() {
        return myState.CHECK_EXPRESSIONS;
    }

    public boolean isCastExpressionsCollapse() {
        return myState.CHECK_EXPRESSIONS;
    }

    public boolean isVarExpressionsCollapse() {
        return myState.VAR_EXPRESSIONS;
    }

    public boolean isGetSetExpressionsCollapse() {
        return myState.GET_SET_EXPRESSIONS;
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
        myState.GET_EXPRESSIONS = state.COMPARING_EXPRESSIONS;
        myState.CHECK_EXPRESSIONS = state.CHECK_EXPRESSIONS;
        myState.RANGE_EXPRESSIONS = state.RANGE_EXPRESSIONS;
        myState.CAST_EXPRESSIONS = state.CAST_EXPRESSIONS;
        myState.VAR_EXPRESSIONS = state.VAR_EXPRESSIONS;
        myState.GET_SET_EXPRESSIONS = state.GET_SET_EXPRESSIONS;
    }

    public static final class State {
        public boolean ARITHMETIC_EXPRESSIONS = true;
        public boolean CONCATENATION_EXPRESSIONS = true;
        public boolean SLICING_EXPRESSIONS = true;
        public boolean COMPARING_EXPRESSIONS = true;
        public boolean GET_EXPRESSIONS = true;
        public boolean RANGE_EXPRESSIONS = true;
        public boolean CHECK_EXPRESSIONS = true;
        public boolean CAST_EXPRESSIONS = true;
        public boolean VAR_EXPRESSIONS = true;
        public boolean GET_SET_EXPRESSIONS = true;

        public State() { /* compiled code */ }
    }
}
