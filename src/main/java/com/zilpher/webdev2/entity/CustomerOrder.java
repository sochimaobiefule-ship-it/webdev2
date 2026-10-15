package com.zilpher.webdev2.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public CustomerOrder() {}

    public CustomerOrder(LocalDate orderDate, String status) {
        this.orderDate = orderDate;
        this.status = status;
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public Long getId() { 
        return id; 
    }public LocalDate getOrderDate() { 
        return orderDate; 
    }public String getStatus() { 
        return status; 
    }public User getUser() { 
        return user; 
    }public List<OrderItem> getOrderItems() { 
        return orderItems; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setOrderDate(LocalDate orderDate) { 
        this.orderDate = orderDate; 
    }public void setStatus(String status) { 
        this.status = status; 
    }public void setUser(User user) { 
        this.user = user; 
    }public void setOrderItems(List<OrderItem> orderItems) { 
        this.orderItems = orderItems; 
    }
}