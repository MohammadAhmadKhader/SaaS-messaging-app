package com.example.multitenant.models.enums;

public enum StripePlan {
    STARTER("starter"),
    PRO("pro"),
    ENTERPRISE("enterprise"),
    FREE("free");

    private final String value;

    StripePlan(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StripePlan fromValue(String value) {
        for (var plan : values()) {
            if (plan.value.equalsIgnoreCase(value)) {
                return plan;
            }
        }

        throw new IllegalArgumentException("invalid StripePlans value: " + value + ". valid values are 'starter' or 'pro' or 'enterprise' or 'free'");
    }

    public static boolean isStarter(String plan) {
        return fromValue(plan).equals(STARTER);
    }

    public static boolean isPro(String plan) {
        return fromValue(plan).equals(PRO);
    }

    public static boolean isEnterprise(String plan) {
        return fromValue(plan).equals(ENTERPRISE);
    }

    public static boolean isFree(String plan) {
        return fromValue(plan).equals(FREE);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
