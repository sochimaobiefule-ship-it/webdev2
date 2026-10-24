package com.declan.productcatalog.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Product() {}

    public Long getId() {
        return id;
    }public String getName() {
        return name;
    }public String getSku() {
        return sku;
    }public BigDecimal getPrice() {
        return price;
    }public Integer getStockQuantity() {
        return stockQuantity;
    }public ProductStatus getStatus() {
        return status;
    }public Category getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }public void setName(String name) {
        this.name = name;
    }public void setSku(String sku) {
        this.sku = sku;
    }public void setPrice(BigDecimal price) {
        this.price = price;
    }public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }public void setStatus(ProductStatus status) {
        this.status = status;
    }public void setCategory(Category category) {
        this.category = category;
    }
}