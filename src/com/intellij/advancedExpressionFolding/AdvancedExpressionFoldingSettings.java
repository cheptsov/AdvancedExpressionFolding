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
        myState.CONTROL_FLOW_MULTI_STATEMENT_CODE_BLOCK = state.CONTROL_FLOW_MULTI_STATEMENT_CODE_BLOCK;
        myState.COMPACT_CONTROL_FLOW_SYNTAX = state.COMPACT_CONTROL_FLOW_SYNTAX;
        myState.SEMICOLONS = state.SEMICOLONS;
    }

    public static final class State {
        private boolean ARITHMETIC_EXPRESSIONS = false;
        private boolean CONCATENATION_EXPRESSIONS = true;
        private boolean SLICING_EXPRESSIONS = true;
        private boolean COMPARING_EXPRESSIONS = true;
        private boolean GET_EXPRESSIONS = true;
        private boolean RANGE_EXPRESSIONS = true;
        private boolean CHECK_EXPRESSIONS = true;
        private boolean CAST_EXPRESSIONS = true;
        private boolean VAR_EXPRESSIONS = true;
        private boolean GET_SET_EXPRESSIONS = true;
        private boolean CONTROL_FLOW_SINGLE_STATEMENT_CODE_BLOCK = false;
        private boolean COMPACT_CONTROL_FLOW_SYNTAX = false;
        private boolean CONTROL_FLOW_MULTI_STATEMENT_CODE_BLOCK = false;
        private boolean SEMICOLONS = false;

        public boolean isArithmeticExpressionsCollapse() {
            return ARITHMETIC_EXPRESSIONS;
        }

        public boolean isConcatenationExpressionsCollapse() {
            return CONCATENATION_EXPRESSIONS;
        }

        public boolean isSlicingExpressionsCollapse() {
            return SLICING_EXPRESSIONS;
        }

        public boolean isComparingExpressionsCollapse() {
            return COMPARING_EXPRESSIONS;
        }

        public boolean isGetExpressionsCollapse() {
            return GET_EXPRESSIONS;
        }

        public boolean isRangeExpressionsCollapse() {
            return RANGE_EXPRESSIONS;
        }

        public boolean isCheckExpressionsCollapse() {
            return CHECK_EXPRESSIONS;
        }

        public boolean isCastExpressionsCollapse() {
            return CHECK_EXPRESSIONS;
        }

        public boolean isVarExpressionsCollapse() {
            return VAR_EXPRESSIONS;
        }

        public boolean isGetSetExpressionsCollapse() {
            return GET_SET_EXPRESSIONS;
        }

        public boolean isControlFlowMultiStatementCodeBlockCollapse() {
            return CONTROL_FLOW_MULTI_STATEMENT_CODE_BLOCK;
        }

        public boolean isControlFlowSingleStatementCodeBlockCollapse() {
            return CONTROL_FLOW_SINGLE_STATEMENT_CODE_BLOCK;
        }

        public boolean isCompactControlFlowSyntaxCollapse() {
            return COMPACT_CONTROL_FLOW_SYNTAX;
        }

        public boolean isSemicolonsCollapse() {
            return SEMICOLONS;
        }

        public void setArithmeticExpressionsCollapse(boolean value) {
            ARITHMETIC_EXPRESSIONS = value;
        }

        public void setConcatenationExpressionsCollapse(boolean value) {
            CONCATENATION_EXPRESSIONS = value;
        }

        public void setSlicingExpressionsCollapse(boolean value) {
            SLICING_EXPRESSIONS = value;
        }

        public void setComparingExpressionsCollapse(boolean value) {
            COMPARING_EXPRESSIONS = value;
        }

        public void setGetExpressionsCollapse(boolean value) {
            GET_EXPRESSIONS = value;
        }

        public void setRangeExpressionsCollapse(boolean value) {
            RANGE_EXPRESSIONS = value;
        }

        public void setCheckExpressionsCollapse(boolean value) {
            CHECK_EXPRESSIONS = value;
        }

        public void setCastExpressionsCollapse(boolean value) {
            CHECK_EXPRESSIONS = value;
        }

        public void setVarExpressionsCollapse(boolean value) {
            VAR_EXPRESSIONS = value;
        }

        public void setGetSetExpressionsCollapse(boolean value) {
            GET_SET_EXPRESSIONS = value;
        }

        public void setControlFlowMultiStatementCodeBlockCollapse(boolean value) {
            CONTROL_FLOW_MULTI_STATEMENT_CODE_BLOCK = value;
        }

        public void setControlFlowSingleStatementCodeBlockCollapse(boolean value) {
            CONTROL_FLOW_SINGLE_STATEMENT_CODE_BLOCK = value;
        }

        public void setCompactControlFlowSyntaxCollapse(boolean value) {
            COMPACT_CONTROL_FLOW_SYNTAX = value;
        }

        public void setSemicolonsCollapse(boolean value) {
            SEMICOLONS = value;
        }
    }
}
