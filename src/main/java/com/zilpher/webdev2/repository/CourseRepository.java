package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zilpher.webdev2.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {}