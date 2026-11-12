package com.zilpher.webdev2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PlaceOrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private boolean simulateFailure = false;

    public Long getUserId() {
        return userId;
    }public Long getProductId() {
        return productId;
    }public Integer getQuantity() {
        return quantity;
    }public boolean isSimulateFailure() {
        return simulateFailure;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }public void setProductId(Long productId) {
        this.productId = productId;
    }public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}