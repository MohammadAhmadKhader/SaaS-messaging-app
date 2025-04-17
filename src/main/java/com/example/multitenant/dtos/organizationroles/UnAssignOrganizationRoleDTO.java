package com.example.multitenant.dtos.organizationroles;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UnAssignOrganizationRoleDTO {
    @Min(value = 1 ,message = "user id can not be less than {value}")
    private Integer userId;
}
