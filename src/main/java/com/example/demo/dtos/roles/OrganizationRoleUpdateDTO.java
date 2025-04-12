package com.example.demo.dtos.roles;

import javax.management.relation.Role;

import com.example.demo.models.OrganizationRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationRoleUpdateDTO {
    
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

    public OrganizationRole toModel() {
        return new OrganizationRole(this.name);
    }
}