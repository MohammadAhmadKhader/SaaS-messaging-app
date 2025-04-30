package com.example.multitenant.models.enums;

public enum LogEventType {
    LOGIN,
    REGISTER,
    RESET_PASSWORD,
    LOGOUT,

    // organizations logs below
    JOIN,
    LEAVE,
    KICK,

    INVITE_SENT,
    INVITE_ACCEPTED,
    INVITE_CANCELLED,

    ROLE_ASSIGN,
    ROLE_UNASSIGN,
    ORG_UPDATED,

    ORG_CHANNEL_CREATED,
    ORG_CHANNEL_UPDATED,
    ORG_CHANNEL_DELETED,

    ORG_CATEGORY_CREATED,
    ORG_CATEGORY_UPDATED,
    ORG_CATEGORY_DELETED,

    ORG_MESSAGE_UPDATED,
    ORG_MESSAGE_DELETED;
}