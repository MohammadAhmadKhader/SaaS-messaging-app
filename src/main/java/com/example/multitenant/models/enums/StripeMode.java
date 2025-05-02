package com.example.multitenant.models.enums;

public enum StripeMode {
    TEST("test"),
    LIVE("live");

    private final String value;

    StripeMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StripeMode fromValue(String value) {
        for (var mode : values()) {
            if (mode.value.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid StripeMode value: " + value + ". Valid values are 'test' or 'live'");
    }

    @Override
    public String toString() {
        return this.value;
    }
}
