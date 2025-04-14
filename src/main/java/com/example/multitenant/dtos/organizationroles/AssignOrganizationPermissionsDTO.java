package com.example.multitenant.dtos.organizationroles;

import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class AssignOrganizationPermissionsDTO {
    @Min(value = 1 ,message = "role id can not be less than {value}")
    private Integer roleId;

    @NotEmpty(message = "one permissions Id is required at least")
    private Set<@NotNull(message = "permission id cannot be null") 
               @Positive(message = "permission id must be positive") Integer> permissionsIds;
}
