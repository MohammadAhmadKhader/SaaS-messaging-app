package com.example.multitenant.dtos.organizationrestrictions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.example.multitenant.common.validators.contract.AtLeastInFuture;
import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.common.validators.contract.ValidTimestamps;
import com.example.multitenant.models.OrgRestriction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AtLeastOneNotNull(fields = {"until", "reason"})
public class OrgRestrictionUpdateDTO {
    @ValidTimestamps(message = "invalid until field time format", allowNull = true)
    private Instant until;

    @Size(max = 128, message = "reason must be at most {max} characters")
    @Size(min = 4, message = "reason must be at least {min} characters")
    private String reason;

    public OrgRestriction toModel() {
        var rest = new OrgRestriction();
        rest.setReason(reason);
        rest.setUntil(until);
        
        return rest;
    }
}
