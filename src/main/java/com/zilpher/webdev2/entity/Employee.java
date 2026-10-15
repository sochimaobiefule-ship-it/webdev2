package com.zilpher.webdev2.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public Employee() {}

    public Employee(String name) {
        this.name = name;
    }

    public Long getId() { 
        return id; 
    }public String getName() { 
        return name; 
    }public Department getDepartment() { 
        return department; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setName(String name) { 
        this.name = name; 
    }public void setDepartment(Department department) { 
        this.department = department; 
    }
}