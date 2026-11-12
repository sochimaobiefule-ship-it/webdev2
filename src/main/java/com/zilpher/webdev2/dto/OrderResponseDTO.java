package com.zilpher.webdev2.dto;

import java.time.LocalDate;

public class OrderResponseDTO {
    private Long orderId;
    private LocalDate orderDate;
    private String status;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private Integer remainingStock;

    public OrderResponseDTO(Long orderId, LocalDate orderDate, String status, Long userId, String username,
            Long productId, String productName, Integer quantity, Double unitPrice, Double totalAmount, Integer remainingStock) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.userId = userId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.remainingStock = remainingStock;
    }

    public Long getOrderId() {
        return orderId;
    }public LocalDate getOrderDate() {
        return orderDate;
    }public String getStatus() {
        return status;
    }public Long getUserId() {
        return userId;
    }public String getUsername() {
        return username;
    }public Long getProductId() {
        return productId;
    }public String getProductName() {
        return productName;
    }public Integer getQuantity() {
        return quantity;
    }public Double getUnitPrice() {
        return unitPrice;
    }public Double getTotalAmount() {
        return totalAmount;
    }public Integer getRemainingStock() {
        return remainingStock;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }public void setStatus(String status) {
        this.status = status;
    }public void setUserId(Long userId) {
        this.userId = userId;
    }public void setUsername(String username) {
        this.username = username;
    }public void setProductId(Long productId) {
        this.productId = productId;
    }public void setProductName(String productName) {
        this.productName = productName;
    }public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }public void setRemainingStock(Integer remainingStock) {
        this.remainingStock = remainingStock;
    }
}