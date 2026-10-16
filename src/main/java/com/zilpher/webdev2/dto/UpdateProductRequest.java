package com.zilpher.webdev2.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class UpdateProductRequest {
    private String name;
    private String sku;

    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    public UpdateProductRequest() {}

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