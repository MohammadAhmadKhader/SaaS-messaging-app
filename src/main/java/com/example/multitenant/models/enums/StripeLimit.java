package com.example.multitenant.models.enums;

public enum StripeLimit {
    CATEGORIES("categories"),
    CATEGORY_CHANNELS("category_channels"),
    MEMBERS("members"),
    ROLES("roles");


    private final String limit;

    StripeLimit(String limit) {
        this.limit = limit;
    }

    public String getLimit() {
        return limit;
    }

    public static StripeLimit fromValue(String value) {
        for (var limit : values()) {
            if (limit.toString().equalsIgnoreCase(value)) {
                return limit;
            }
        }
        
        throw new IllegalArgumentException("invalid StripePlans value: " + value + ". valid values are 'categories' or 'category_channels' or 'members' or 'roles");
    }

    @Override
    public String toString() {
        return this.limit;
    }
}
