package com.example.demo.dtos.globalpermissions;

import com.example.demo.models.GlobalPermission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalPermissionUpdateDTO {
    @NotBlank(message = "name can not be empty")
    @Size(max = 64, message = "name must be at most {max}")
    @Size(min = 2, message = "name must be at least {min}")
    private String name;

     public GlobalPermission toModel() {
        return new GlobalPermission(this.name);
    }
}
