package com.example.multitenant.dtos.organizations;

import org.springframework.web.multipart.MultipartFile;

import com.example.multitenant.common.validators.contract.AllowedFileTypes;
import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.common.validators.contract.FileSize;
import com.example.multitenant.models.Organization;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AtLeastOneNotNull(fields = {"name"," industry", "image"})
public class OrganizationUpdateDTO {
    @Size(max = 128, message = "name must be at most {max}")
    @Size(min = 1, message = "name must be at least {min}")
    private String name;

    @Size(max = 128, message = "industry must be at most {max}")
    @Size(min = 1, message = "industry must be at least {min}")
    private String industry;

    @AllowedFileTypes(
        types = { "image/jpeg", "image/png", "image/avif", "image/webp"},
        message = "only JPEG, PNG, AVIF, and WEBP images are allowed"
    )
    @FileSize(max = 1 * 1024 * 1024, message = "file must be 1MB or smaller")
    
    private MultipartFile image;

    public Organization toModel() {
        return new Organization(this.name, this.industry);
    }
}