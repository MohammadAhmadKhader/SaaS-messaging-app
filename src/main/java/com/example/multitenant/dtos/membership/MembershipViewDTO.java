package com.example.multitenant.dtos.membership;

import java.time.Instant;

import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.dtos.users.*;
import com.example.multitenant.models.Membership;
import com.example.multitenant.models.User;
import lombok.*;

@Getter
@Setter
public class MembershipViewDTO {
    private Integer organizationId;
    private long userId;
    private Instant joinedAt;
    private OrgViewDTO organization;
    private UserOrgViewDTO user;
    
    public MembershipViewDTO(Membership memebership) {
        setOrganizationId(memebership.getId().getOrganizationId());
        setUserId(memebership.getId().getUserId());
        setJoinedAt(memebership.getJoinedAt());
        setOrganization(memebership.getOrganization().toViewDTO());
        setUser(memebership.getUser().toOrganizationViewDTO(memebership));
    }
}
