package com.example.multitenant.dtos.organizationroles;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.OrganizationRole;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AtLeastOneNotNull(fields = {"name", "displayName"})
public class OrganizationRoleUpdateDTO {
    
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String displayName;

    public OrganizationRole toModel() {
        return new OrganizationRole(this.getName(), this.getDisplayName());
    }
}