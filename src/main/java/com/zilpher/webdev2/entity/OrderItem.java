package com.zilpher.webdev2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public OrderItem() {}

    public OrderItem(Integer quantity, Double price) {
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() { 
        return id; 
    }public Integer getQuantity() { 
        return quantity; 
    }public Double getPrice() { 
        return price; 
    }public CustomerOrder getOrder() { 
        return order; 
    }public Product getProduct() { 
        return product; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }public void setPrice(Double price) { 
        this.price = price; 
    }public void setOrder(CustomerOrder order) { 
        this.order = order; 
    }public void setProduct(Product product) { 
        this.product = product; 
    }
}