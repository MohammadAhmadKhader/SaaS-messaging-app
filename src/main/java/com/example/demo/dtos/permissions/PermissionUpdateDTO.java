package com.example.demo.dtos.permissions;

import com.example.demo.models.Permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionUpdateDTO {
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

     public Permission toModel() {
        return new Permission(this.name);
    }
}
