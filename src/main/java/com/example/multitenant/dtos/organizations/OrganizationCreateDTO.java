package com.example.multitenant.dtos.organizations;

import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.common.validators.contract.AllowedFileTypes;
import com.example.multitenant.common.validators.contract.FileSize;
import com.example.multitenant.models.Organization;

import jakarta.validation.constraints.*;

public record OrganizationCreateDTO(
    @NotBlank(message = "name can not be empty")
    @Size(max = 128, message = "name must be at most {max}")
    @Size(min = 1, message = "name must be at least {min}")
    String name,

    @NotBlank(message = "industry can not be empty")
    @Size(max = 128, message = "industry must be at most {max}")
    @Size(min = 1, message = "industry must be at least {min}")
    String industry,

    @AllowedFileTypes(
        types = { "image/jpeg", "image/png", "image/avif", "image/webp"},
        message = "only JPEG, PNG, AVIF, and WEBP images are allowed"
    )
    @FileSize(max = 1 * 1024 * 1024, message = "file must be 1MB or smaller")
    MultipartFile image
){
    public Organization toModel() {
        return new Organization(this.name, this.industry);
    }
}

