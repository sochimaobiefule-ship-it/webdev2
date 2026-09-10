package com.zilpher.webdev2.model;

import jakarta.validation.constraints.*;

public class Product {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Stock level is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stock;

    public Product() {}

    public Product(Long id, String name, String category, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { 
        return id; 
    }public String getName() { 
        return name; 
    }public String getCategory() { 
        return category; 
    }public Double getPrice() { 
        return price; 
    }public Integer getStock() { 
        return stock; 
    }

    public void setId(Long id) { 
        this.id = id;
    }public void setName(String name) { 
        this.name = name; 
    }public void setCategory(String category) { 
        this.category = category; 
    }public void setPrice(Double price) { 
        this.price = price; 
    }public void setStock(Integer stock) { 
        this.stock = stock; 
    }
}