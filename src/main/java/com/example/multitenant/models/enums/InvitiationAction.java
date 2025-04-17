package com.example.multitenant.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum InvitiationAction {
    SEND,
    CANCEL,
    ACCEPT,
    REJECT;

    @JsonCreator
    public static InvitiationAction fromString(String value) {
        return InvitiationAction.valueOf(value.toUpperCase().trim());
    }
}
