package com.example.multitenant.dtos.globalroles;

import com.example.multitenant.models.GlobalRole;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class GlobalRoleCreateDTO {

    @NotNull(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    @NotNull(message = "display name can not be empty")
    @Size(max = 64, message = "display name must be at most {max}")
    @Size(min = 2, message = "display name must be at least {min}")
    private String displayName;

    public GlobalRole toModel() {
        return new GlobalRole(this.getName(), this.getDisplayName());
    }
}
