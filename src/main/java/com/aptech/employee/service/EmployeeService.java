package com.aptech.employee.service;

import com.aptech.employee.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    // Basic CRUD operations
    List<Employee> findAll();
    Optional<Employee> findById(Long id);
    Employee save(Employee employee);
    Employee update(Long id, Employee employee);
    void deleteById(Long id);

    // Search and validation
    List<Employee> searchByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndNotId(String name, Long id);

    // Additional operations
    List<Employee> findAllOrderByName();
    List<Employee> findAllOrderBySalary();
    long count();
}