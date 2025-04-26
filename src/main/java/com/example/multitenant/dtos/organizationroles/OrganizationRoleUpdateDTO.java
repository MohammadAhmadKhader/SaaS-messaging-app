package com.example.multitenant.dtos.organizationroles;

import com.example.multitenant.models.OrganizationRole;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class OrganizationRoleUpdateDTO {
    
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    public OrganizationRole toModel() {
        return new OrganizationRole(this.name);
    }
}