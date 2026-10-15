package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {}