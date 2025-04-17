package com.example.multitenant.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum InvitationStatus {
    CANCELLED,
    REJECTED,
    ACCEPTED,
    PENDING;
}
