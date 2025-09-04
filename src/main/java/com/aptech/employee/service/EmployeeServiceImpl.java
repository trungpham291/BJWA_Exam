package com.aptech.employee.service;

import com.aptech.employee.entity.Employee;
import com.aptech.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        logger.debug("Finding all employees");
        return employeeRepository.findAllOrderByName();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        logger.debug("Finding employee by id: {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public Employee save(Employee employee) {
        logger.debug("Saving employee: {}", employee);

        // Trim name before saving
        if (employee.getName() != null) {
            employee.setName(employee.getName().trim());
        }

        // Check for duplicate name
        if (existsByName(employee.getName())) {
            throw new IllegalArgumentException("Employee with name '" + employee.getName() + "' already exists");
        }

        Employee saved = employeeRepository.save(employee);
        logger.info("Employee saved successfully with id: {}", saved.getId());
        return saved;
    }

    @Override
    public Employee update(Long id, Employee employee) {
        logger.debug("Updating employee with id: {} - {}", id, employee);

        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found with id: " + id);
        }

        // Trim name before updating
        if (employee.getName() != null) {
            employee.setName(employee.getName().trim());
        }

        // Check for duplicate name (excluding current employee)
        if (existsByNameAndNotId(employee.getName(), id)) {
            throw new IllegalArgumentException("Employee with name '" + employee.getName() + "' already exists");
        }

        employee.setId(id);
        Employee updated = employeeRepository.save(employee);
        logger.info("Employee updated successfully: {}", updated);
        return updated;
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting employee with id: {}", id);

        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
        logger.info("Employee deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> searchByName(String name) {
        logger.debug("Searching employees by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        return employeeRepository.findByNameContainingIgnoreCase(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return employeeRepository.existsByNameIgnoreCase(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndNotId(String name, Long id) {
        if (name == null || name.trim().isEmpty() || id == null) {
            return false;
        }
        return employeeRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAllOrderByName() {
        return employeeRepository.findAllOrderByName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAllOrderBySalary() {
        return employeeRepository.findAllOrderBySalaryDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return employeeRepository.count();
    }
}