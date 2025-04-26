package com.example.multitenant.dtos.globalpermissions;

import com.example.multitenant.models.GlobalPermission;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class GlobalPermissionCreateDTO {
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    public GlobalPermission toModel() {
        return new GlobalPermission(this.name);
    }
}
