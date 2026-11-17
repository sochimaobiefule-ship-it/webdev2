package com.zilpher.webdev2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}