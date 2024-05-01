package com.enums;

public enum JiraStatus {
    PASS(0), FAIL(3);
    private final int value;

    private JiraStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
