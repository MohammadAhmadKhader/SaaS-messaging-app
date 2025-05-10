package com.example.multitenant.dtos.organizationrestrictions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.example.multitenant.common.validators.contract.AtLeastInFuture;
import com.example.multitenant.models.OrganizationRestriction;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrgRestrictionCreateDTO {
    @NotNull(message = "user id is required")
    @Min(value = 1 ,message = "user id can not be less than {value}")
    private Long userId;
    
    @NotNull(message = "until is required")
    @AtLeastInFuture(amount = 1, unit = ChronoUnit.DAYS, message = "restriction must be at least 1 days in the future")
    private Instant until;

    @NotBlank(message = "reason is required")
    @Size(max = 128, message = "reason must be at most {max} characters")
    @Size(min = 4, message = "reason must be at least {min} characters")
    private String reason;

    public OrganizationRestriction toModel() {
        var rest = new OrganizationRestriction();
        rest.setUntil(until);
        rest.setReason(reason);
    
        return rest;
    }
}
