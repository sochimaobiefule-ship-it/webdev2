package com.zilpher.webdev2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double price;

    public Product() {}

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() { 
        return id; 
    }public String getName() { 
        return name; 
    }public Double getPrice() { 
        return price; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setName(String name) { 
        this.name = name; 
    }public void setPrice(Double price) { 
        this.price = price; 
    }
}