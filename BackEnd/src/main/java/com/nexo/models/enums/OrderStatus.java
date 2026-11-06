package com.nexo.models.enums;

public enum OrderStatus {

    IN_PROGRESS("in_progress"),

    // Value representing pendent status
    PENDING("pending"),

    // Value representing completed status
    COMPLETED("completed");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
