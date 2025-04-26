package com.example.multitenant.dtos.organizations;

import java.time.Instant;
import java.util.List;

import com.example.multitenant.dtos.users.*;
import com.example.multitenant.models.Organization;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class OrganizationWithUserRolesViewDTO {
    private Integer id;

    private String name;

    private String industry;

    private Instant createdAt;

    private List<UserWithoutRolesViewDTO> users;

    public OrganizationWithUserRolesViewDTO(Organization org) {
        setId(org.getId());
        setName(org.getName());
        setIndustry(org.getIndustry());
        setCreatedAt(org.getCreatedAt());
    }

    public OrganizationWithUserRolesViewDTO() {
        
    }
}
