package com.zilpher.webdev2.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate enrollmentDate;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Enrollment() {}

    public Enrollment(Student student, Course course, String grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
        this.enrollmentDate = LocalDate.now();
    }

    public Long getId() { 
        return id; 
    }public LocalDate getEnrollmentDate() { 
        return enrollmentDate; 
    }public String getGrade() { 
        return grade; 
    }public Student getStudent() { 
        return student; 
    }public Course getCourse() { 
        return course; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setEnrollmentDate(LocalDate enrollmentDate) { 
        this.enrollmentDate = enrollmentDate; 
    }public void setGrade(String grade) { 
        this.grade = grade; 
    }public void setStudent(Student student) { 
        this.student = student; 
    }public void setCourse(Course course) { 
        this.course = course; 
    }
}