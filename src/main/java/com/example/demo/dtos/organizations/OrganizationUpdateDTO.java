package com.example.demo.dtos.organizations;

import com.example.demo.models.Organization;

import jakarta.validation.constraints.Size;

public record OrganizationUpdateDTO(
    @Size(max = 128, message = "name must be at most {max}")
    @Size(min = 1, message = "name must be at least {min}")
    String name,

    @Size(max = 128, message = "industry must be at most {max}")
    @Size(min = 1, message = "industry must be at least {min}")
    String industry
){
    public Organization toModel() {
        return new Organization(this.name, this.industry);
    }
}

