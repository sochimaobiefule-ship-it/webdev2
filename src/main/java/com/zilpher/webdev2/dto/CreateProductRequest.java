package com.zilpher.webdev2.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    public CreateProductRequest() {}

    public String getName() {
        return name;
    }public String getSku() {
        return sku;
    }public BigDecimal getPrice() {
        return price;
    }public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setName(String name) {
        this.name = name;
    }public void setSku(String sku) {
        this.sku = sku;
    }public void setPrice(BigDecimal price) {
        this.price = price;
    }public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}