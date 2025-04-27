package com.example.multitenant.dtos.organizationroles;

import com.example.multitenant.models.OrganizationRole;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class OrganizationRoleCreateDTO {

    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    @NotBlank(message = "display name can not be empty")
    @Size(max = 64, message = "display name must be at most {max}")
    @Size(min = 2, message = "display name must be at least {min}")
    private String displayName;

    public OrganizationRole toModel() {
        return new OrganizationRole(this.getName(), this.getDisplayName());
    }
}
