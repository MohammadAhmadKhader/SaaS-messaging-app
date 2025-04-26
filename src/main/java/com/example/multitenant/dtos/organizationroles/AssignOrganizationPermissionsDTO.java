package com.example.multitenant.dtos.organizationroles;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
public class AssignOrganizationPermissionsDTO {
    @NotNull(message = "role id is required")
    @Min(value = 1 ,message = "role id can not be less than {value}")
    private Integer roleId;

    @NotEmpty(message = "one permissions id is required at least")
    private Set<@NotNull(message = "permission id cannot be null") 
               @Positive(message = "permission id must be positive") Integer> permissionsIds;
}
