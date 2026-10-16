package com.zilpher.webdev2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.zilpher.webdev2.entity.Course;
import com.zilpher.webdev2.entity.Enrollment;
import com.zilpher.webdev2.entity.Student;
import com.zilpher.webdev2.repository.CourseRepository;
import com.zilpher.webdev2.repository.EnrollmentRepository;
import com.zilpher.webdev2.repository.StudentRepository;

// @Component
public class Task2TestRunner implements CommandLineRunner {

    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("TASK 2 TEST RUNNER START");

        Student student = studentRepository.save(new Student("John Doe"));
        Course course = courseRepository.save(new Course("WEBDEV2"));

        Enrollment e1 = new Enrollment(student, course, "A");
        enrollmentRepository.save(e1);
        System.out.println("First enrollment saved successfully");

        try {
            Enrollment e2 = new Enrollment(student, course, "B");
            enrollmentRepository.saveAndFlush(e2);
        } catch (Exception e) {
            System.out.println("Caught duplicate enrollment constraint violation: " + e.getMessage());
        }

        System.out.println("TASK 2 TEST RUNNER END");
    }
}