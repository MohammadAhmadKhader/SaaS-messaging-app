package com.example.multitenant.dtos.categories;

import com.example.multitenant.common.validators.contract.AllDifferentIntegerFields;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllDifferentIntegerFields(fieldNames = {"categoryId1", "categoryId2"})
public class CategoryOrderSwapDTO {

    @Min(value = 1, message = "first category can not be less than {value}")
    @NotNull(message = "first category id is required")
    private Integer categoryId1;

    @Min(value = 1, message = "second category can not be less than {value}")
    @NotNull(message = "second category id is required")
    private Integer categoryId2;
}