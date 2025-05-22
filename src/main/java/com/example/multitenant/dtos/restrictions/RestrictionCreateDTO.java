package com.example.multitenant.dtos.restrictions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.example.multitenant.common.validators.contract.AtLeastInFuture;
import com.example.multitenant.models.Restriction;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestrictionCreateDTO {
    @NotNull(message = "user id is required")
    @Min(value = 1 ,message = "user id can not be less than {value}")
    private Long userId;
    
    @NotNull(message = "until is required")
    @AtLeastInFuture(amount = 1, unit = ChronoUnit.DAYS, message = "restriction must be at least 1 days in the future")
    private Instant until;

    @NotNull(message = "reason is required")
    @Size(max = 64, message = "reason must be at most {max} characters")
    @Size(min = 4, message = "reason must be at least {min} characters")
    private String reason;

    public Restriction toModel() {
        var rest = new Restriction();
        rest.setReason(reason);
        rest.setUntil(until);
        
        return rest;
    }
}
