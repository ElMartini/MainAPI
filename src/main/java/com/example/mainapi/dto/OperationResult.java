package com.example.mainapi.dto;

public class OperationResult {
    private boolean isRollback;
    private boolean result;

    public boolean isRollback() {
        return isRollback;
    }

    public void setRollback(boolean rollback) {
        isRollback = rollback;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
