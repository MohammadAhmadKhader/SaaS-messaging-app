package com.example.multitenant.dtos.categories;

import com.example.multitenant.models.Category;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {
    @NotNull(message = "name is required")
    @Size(min = 2, message = "name can not be less than {min} characters")
    @Size(max = 32, message = "name can not be more than {max} characters")
    private String name;
    
    public Category toModel() {
        var category = new Category();
        category.setName(this.getName());
        return category;
    }
}
