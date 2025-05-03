package com.example.multitenant.dtos.organizations;

import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.models.Organization;

import jakarta.validation.constraints.*;

public record OrganizationUpdateDTO(
    @Size(max = 128, message = "name must be at most {max}")
    @Size(min = 1, message = "name must be at least {min}")
    String name,

    @Size(max = 128, message = "industry must be at most {max}")
    @Size(min = 1, message = "industry must be at least {min}")
    String industry,

    MultipartFile image
){
    public Organization toModel() {
        return new Organization(this.name, this.industry);
    }
}

