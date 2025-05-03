package com.example.multitenant.models.enums;

public enum FilesPath {
    USERS_AVATARS("/users/avatars"),
    ORGS_IMAGES("/organizations/images");

    private final String value;

    FilesPath(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FilesPath fromValue(String value) {
        for (var mode : values()) {
            if (mode.value.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("invalid FilesPath value: " + value + ". valid values are 'test' or 'live'");
    }

    @Override
    public String toString() {
        return this.value;
    }
}