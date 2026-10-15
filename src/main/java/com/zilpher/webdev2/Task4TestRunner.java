package com.zilpher.webdev2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.entity.Course;
import com.zilpher.webdev2.entity.Department;
import com.zilpher.webdev2.entity.Employee;
import com.zilpher.webdev2.entity.Student;
import com.zilpher.webdev2.repository.CourseRepository;
import com.zilpher.webdev2.repository.DepartmentRepository;
import com.zilpher.webdev2.repository.StudentRepository;

@Component
public class Task4TestRunner implements CommandLineRunner {

    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("TASK 4 TEST RUNNER START");

        System.out.println("\nStep 1: Testing orphanRemoval on Department -> Employee");
        Department dept = new Department("HR");
        Employee emp1 = new Employee("Alice HR");
        Employee emp2 = new Employee("Charlie HR");
        dept.addEmployee(emp1);
        dept.addEmployee(emp2);

        Department savedDept = departmentRepository.saveAndFlush(dept);

        savedDept.removeEmployee(emp1);
        departmentRepository.saveAndFlush(savedDept);
        System.out.println("Employee removed from collection and orphan deleted successfully");

        System.out.println("\nStep 2: Testing Deleting Student without Deleting Shared Course");
        Course course = courseRepository.save(new Course("DATABASES"));
        Student student = new Student("David Student");
        studentRepository.save(student);

        studentRepository.delete(student);
        studentRepository.flush();

        boolean courseExists = courseRepository.existsById(course.getId());
        System.out.println("Student deleted. Does shared Course still exist in DB? " + courseExists);

        System.out.println("TASK 4 TEST RUNNER END");
    }
}