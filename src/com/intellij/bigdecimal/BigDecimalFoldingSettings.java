package com.intellij.bigdecimal;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "BigDecimalFoldingSettings", storages = @Storage("editor.codeinsight.xml"))
public class BigDecimalFoldingSettings implements PersistentStateComponent<BigDecimalFoldingSettings.State> {
    private final State myState = new State();

    @NotNull
    @Override
    public State getState() {
        return myState;
    }

    public boolean isCollapseOperations() {
        return myState.COLLAPSE_OPERATIONS;
    }

    @NotNull
    public static BigDecimalFoldingSettings getInstance() {
        return ServiceManager.getService(BigDecimalFoldingSettings.class);
    }     
    
    @Override
    public void loadState(State state) {
        myState.COLLAPSE_OPERATIONS = state.COLLAPSE_OPERATIONS;
    }

    public static final class State {
        public boolean COLLAPSE_OPERATIONS = true;

        public State() { /* compiled code */ }
    }
}
