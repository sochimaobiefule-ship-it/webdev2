package com.zilpher.webdev2.dto;

import java.math.BigDecimal;

public class ProductResponseDTO {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;

    public ProductResponseDTO() {}

    public ProductResponseDTO(Long id, String name, String sku, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

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
    }
}