package com.example.multitenant.dtos.organizationrestrictions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgRestrictionsFilter {
    private Integer organizationId;
    private Boolean isActive;
    private Long userId;
    private Long createdById;
    private String reason;
}
