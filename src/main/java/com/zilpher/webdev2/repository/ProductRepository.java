package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}