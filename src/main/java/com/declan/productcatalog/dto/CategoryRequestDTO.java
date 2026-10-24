package com.declan.productcatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequestDTO {
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    public CategoryRequestDTO() {
    }

    public String getName() {
        return name;
    }public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }public void setDescription(String description) {
        this.description = description;
    }
}