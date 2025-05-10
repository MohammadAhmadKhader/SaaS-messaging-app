package com.example.multitenant.dtos.organizationrestrictions;

import java.time.Instant;

import com.example.multitenant.dtos.users.UserWithoutRolesViewDTO;
import com.example.multitenant.models.OrganizationRestriction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgRestrictionViewDTO {
    private Integer id;
    private UserWithoutRolesViewDTO user;
    private Instant until;
    private String reason;
    private Instant createdAt;
    private UserWithoutRolesViewDTO createdBy;

    public OrgRestrictionViewDTO(OrganizationRestriction rest) {
        setId(rest.getId());
        var user = rest.getUser() == null ? null : rest.getUser().toViewWithoutRolesDTO();
        setUser(user);
        setUntil(rest.getUntil());
        setReason(rest.getReason());
        setCreatedAt(rest.getCreatedAt());
        var createdBy = rest.getCreatedBy() == null ? null : rest.getCreatedBy().toViewWithoutRolesDTO();
        setCreatedBy(createdBy);
    }
}