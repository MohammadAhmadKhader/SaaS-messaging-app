package com.example.multitenant.dtos.organizationpermissions;

import com.example.multitenant.models.OrgPermission;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class OrgPermissionCreateDTO {
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    public OrgPermission toModel() {
        return new OrgPermission(this.name);
    }
}
