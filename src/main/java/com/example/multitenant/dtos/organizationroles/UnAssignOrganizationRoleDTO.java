package com.example.multitenant.dtos.organizationroles;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UnAssignOrganizationRoleDTO {
    @NotNull(message = "user id cannot be null")
    @Min(value = 1 ,message = "user id can not be less than {value}")
    private Integer userId;
}
