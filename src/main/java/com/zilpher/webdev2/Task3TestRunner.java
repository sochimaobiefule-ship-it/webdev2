package com.zilpher.webdev2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.zilpher.webdev2.entity.Department;
import com.zilpher.webdev2.entity.Employee;
import com.zilpher.webdev2.repository.DepartmentRepository;
import com.zilpher.webdev2.service.EmployeeService;

// @Component
public class Task3TestRunner implements CommandLineRunner {

    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private EmployeeService employeeService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("TASK 3 TEST RUNNER START");

        Department dept = new Department("Research & Development");
        Employee emp = new Employee("Bob Developer");
        dept.addEmployee(emp);
        Department savedDept = departmentRepository.save(dept);
        Long empId = savedDept.getEmployees().get(0).getId();

        System.out.println("\nStep 1: Testing Non-Transactional Lazy Load");
        try {
            employeeService.getDepartmentNameLazyUntransactional(empId);
        } catch (Exception e) {
            System.out.println("Caught expected LazyInitializationException: " + e.getClass().getName() + " - " + e.getMessage());
        }

        System.out.println("\nStep 2: Testing @Transactional Fix");
        String deptNameA = employeeService.getDepartmentNameTransactional(empId);
        System.out.println("Department name retrieved via @Transactional: " + deptNameA);

        System.out.println("\nStep 3: Testing JOIN FETCH Fix");
        String deptNameB = employeeService.getDepartmentNameFetchJoin(empId);
        System.out.println("Department name retrieved via JOIN FETCH: " + deptNameB);

        System.out.println("TASK 3 TEST RUNNER END");
    }
}