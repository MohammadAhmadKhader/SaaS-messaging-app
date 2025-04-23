package com.example.multitenant.dtos.organizationroles;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AssignOrganizationRoleDTO {
    @NotNull(message = "role id is required")
    @Min(value = 1 ,message = "role id can not be less than {value}")
    private Integer roleId;

    @NotNull(message = "user id is required")
    @Min(value = 1 ,message = "user id can not be less than {value}")
    private Integer userId;
}
