package com.example.multitenant.dtos.categories;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {
    @NotBlank(message = "name is required")
    private String name;
    
    public Category toModel() {
        var category = new Category();
        category.setName(this.getName());

        return category;
    }
}
