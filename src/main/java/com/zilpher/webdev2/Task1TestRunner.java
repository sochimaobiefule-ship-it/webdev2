package com.zilpher.webdev2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.entity.Department;
import com.zilpher.webdev2.entity.Employee;
import com.zilpher.webdev2.repository.DepartmentRepository;

@Component
public class Task1TestRunner implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Department dept = new Department("Engineering");
        dept.addEmployee(new Employee("Alice"));
        dept.addEmployee(new Employee("Bob"));

        departmentRepository.save(dept);
        System.out.println(">>> Saved Department with Employees successfully!");
    }
}