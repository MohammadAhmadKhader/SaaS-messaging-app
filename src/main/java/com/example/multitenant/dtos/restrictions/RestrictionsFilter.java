package com.example.multitenant.dtos.restrictions;

import lombok.Getter;

@Getter
public class RestrictionsFilter {
    private Boolean isActive;
    private Long userId;
    private Long createdById;
    private String reason;
}