package com.example.multitenant.dtos.globalroles;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.GlobalRole;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AtLeastOneNotNull(fields = {"name", "displayName"})
public class GlobalRoleUpdateDTO {
    
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    @Size(max = 64, message = "display name must be at most {max}")
    @Size(min = 2, message = "display name must be at least {min}")
    private String displayName;

    public GlobalRole toModel() {
        return new GlobalRole(this.getName(), this.getDisplayName());
    }
}