package com.example.multitenant.dtos.globalroles;

import com.example.multitenant.models.GlobalRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalRoleUpdateDTO {
    
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    public GlobalRole toModel() {
        return new GlobalRole(this.name);
    }
}