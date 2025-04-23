package com.example.multitenant.dtos.globalroles;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class GlobalAssignPermissionsDTO {
     @NotEmpty(message = "one permissions id is required at least")
    Set<@NotNull(message = "permission id cannot be null")
    @Positive(message = "permission id must be positive") Integer> permissionsIds;
}
