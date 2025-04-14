package com.example.multitenant.dtos.membership;

import java.time.Instant;

import com.example.multitenant.dtos.organizations.OrganizationViewDTO;
import com.example.multitenant.dtos.users.UserOrganizationViewDTO;
import com.example.multitenant.dtos.users.UserViewDTO;
import com.example.multitenant.models.Membership;
import com.example.multitenant.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipViewDTO {
    private Integer organizationId;
    private long userId;
    private Instant joinedAt;
    private OrganizationViewDTO organization;
    private UserOrganizationViewDTO user;
    
    public MembershipViewDTO(Membership memebership) {
        setOrganizationId(memebership.getId().getOrganizationId());
        setUserId(memebership.getId().getUserId());
        setJoinedAt(memebership.getJoinedAt());
        setOrganization(memebership.getOrganization().toViewDTO());
        setUser(memebership.getUser().toOrganizationViewDTO(memebership));
    }
}
