package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByProductId(Long productId);
}