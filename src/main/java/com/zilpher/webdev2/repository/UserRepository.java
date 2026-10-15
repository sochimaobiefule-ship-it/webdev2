package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}