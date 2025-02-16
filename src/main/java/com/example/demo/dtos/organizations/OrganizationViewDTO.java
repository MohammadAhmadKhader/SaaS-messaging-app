package com.example.demo.dtos.organizations;

import java.time.Instant;

import com.example.demo.models.Organization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationViewDTO {
    private Integer id;

    private String name;

    private String industry;

    private Instant createdAt;

    public OrganizationViewDTO(Organization org) {
        setId(org.getId());
        setName(org.getName());
        setIndustry(org.getIndustry());
        setCreatedAt(org.getCreatedAt());
    }
}
