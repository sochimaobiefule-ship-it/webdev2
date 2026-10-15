package com.zilpher.webdev2.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Course() {}

    public Course(String title) {
        this.title = title;
    }

    public Long getId() { 
        return id; 
    }public String getTitle() { 
        return title; 
    }public List<Enrollment> getEnrollments() { 
        return enrollments; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setTitle(String title) { 
        this.title = title; 
    }public void setEnrollments(List<Enrollment> enrollments) { 
        this.enrollments = enrollments; 
    }
}