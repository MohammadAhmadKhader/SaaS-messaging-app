package com.example.multitenant.dtos.organizations;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class OrganizationTransferOwnershipDTO {
    @Min(value = 1, message = "new owner id can not be less than {value}")
    @NotNull(message = "new owner id is required")
    private Long newOwnerId;
}
