package com.declan.productcatalog.dto;

import java.math.BigDecimal;

import com.declan.productcatalog.model.ProductStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9]+-[A-Z0-9]+$", message = "SKU must match format like 'PROD-101'")
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", message = "Price must be at least 1")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private ProductStatus status;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    public ProductRequestDTO() {
    }

    public String getName() {
        return name;
    }public String getSku() {
        return sku;
    }public BigDecimal getPrice() {
        return price;
    }public Integer getStockQuantity() {
        return stockQuantity;
    }public ProductStatus getStatus() {
        return status;
    }public Long getCategoryId() {
        return categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }public void setSku(String sku) {
        this.sku = sku;
    }public void setPrice(BigDecimal price) {
        this.price = price;
    }public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }public void setStatus(ProductStatus status) {
        this.status = status;
    }public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}