package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {}