package com.example.multitenant.dtos.organizations;

import com.example.multitenant.models.Organization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgSearchDTO {
    private Integer id;
    private String name;
    private String imageUrl;

    public OrgSearchDTO(Organization org) {
        setId(org.getId());
        setName(org.getName());
        setImageUrl(org.getImageUrl());
    }
}
