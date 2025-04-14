package com.example.multitenant.dtos.organizationroles;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class UnAssignOrganizationPermissionsDTO {
    @NotEmpty(message = "one permissions Id is required at least")
    private Set<@NotNull(message = "permission id cannot be null") 
               @Positive(message = "permission id must be positive") Integer> permissionsIds;
}
