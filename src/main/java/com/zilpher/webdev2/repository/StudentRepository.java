package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {}