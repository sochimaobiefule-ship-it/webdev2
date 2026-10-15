package com.zilpher.webdev2.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student() {}

    public Student(String name) {
        this.name = name;
    }

    public Long getId() { 
        return id; 
    }public String getName() { 
        return name; 
    }public List<Enrollment> getEnrollments() { 
        return enrollments; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setName(String name) { 
        this.name = name; 
    }public void setEnrollments(List<Enrollment> enrollments) { 
        this.enrollments = enrollments; 
    }
}