package com.example.demo.dtos.organizationpermissions;

import java.security.Permission;

import com.example.demo.models.OrganizationPermission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationPermissionCreateDTO {
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    public OrganizationPermission toModel() {
        return new OrganizationPermission(this.name);
    }
}
