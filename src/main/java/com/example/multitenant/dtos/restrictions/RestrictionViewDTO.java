package com.example.multitenant.dtos.restrictions;

import java.time.Instant;

import com.example.multitenant.dtos.users.UserViewDTO;
import com.example.multitenant.dtos.users.UserWithoutRolesViewDTO;
import com.example.multitenant.models.Restriction;

import lombok.*;

@Getter
@Setter
public class RestrictionViewDTO {
    private Integer id;
    private UserWithoutRolesViewDTO user;
    private Instant until;
    private String reason;
    private Instant createdAt;
    private UserWithoutRolesViewDTO createdBy;

    public RestrictionViewDTO(Restriction rest) {
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
