package com.zilpher.webdev2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.entity.Employee;
import com.zilpher.webdev2.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public String getDepartmentNameLazyUntransactional(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return employee.getDepartment().getName();
    }

    @Transactional
    public String getDepartmentNameTransactional(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return employee.getDepartment().getName();
    }
    
    public String getDepartmentNameFetchJoin(Long employeeId) {
        Employee employee = employeeRepository.findByIdWithDepartment(employeeId).orElseThrow();
        return employee.getDepartment().getName();
    }
}