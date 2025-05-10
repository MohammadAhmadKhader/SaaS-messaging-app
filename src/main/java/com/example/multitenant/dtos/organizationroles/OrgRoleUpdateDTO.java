package com.example.multitenant.dtos.organizationroles;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.OrgRole;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AtLeastOneNotNull(fields = {"name", "displayName"})
public class OrgRoleUpdateDTO {
    
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String displayName;

    public OrgRole toModel() {
        return new OrgRole(this.getName(), this.getDisplayName());
    }
}