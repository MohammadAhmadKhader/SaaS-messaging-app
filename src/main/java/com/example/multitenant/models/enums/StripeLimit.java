package com.example.multitenant.models.enums;

public enum StripeLimit {
    CATEGORIES("max-categories"),
    CATEGORY_CHANNELS("max-category-channels"),
    MEMBERS("max-members"),
    ROLES("max-roles");


    private final String limit;

    StripeLimit(String limit) {
        this.limit = limit;
    }

    public String getLimit() {
        return limit;
    }

    public static StripeLimit fromValue(String value) {
        for (var limit : values()) {
            if (limit.limit.equalsIgnoreCase(value)) {
                return limit;
            }
        }
        
        throw new IllegalArgumentException("invalid StripePlans value: " + value + ". valid values are 'max-categories' or 'max-category-channels' or 'max-members' or 'max-roles");
    }

    @Override
    public String toString() {
        return this.limit;
    }
}
