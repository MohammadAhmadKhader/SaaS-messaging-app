package com.example.multitenant.dtos.organizationroles;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
public class OrgUnAssignRoleDTO {
    @NotNull(message = "user id cannot be null")
    @Min(value = 1 ,message = "user id can not be less than {value}")
    private Integer userId;
}
