package com.aptech.employee.repository;

import com.aptech.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Search employees by name (case-insensitive, partial match)
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> findByNameContainingIgnoreCase(@Param("name") String name);

    // Check if employee exists by name (case-insensitive, exact match)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Employee e WHERE LOWER(e.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    // Find employee by exact name (case-insensitive)
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) = LOWER(:name)")
    Optional<Employee> findByNameIgnoreCase(@Param("name") String name);

    // Check if name exists for different employee (for update validation)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Employee e WHERE LOWER(e.name) = LOWER(:name) AND e.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    // Find employees by age range
    List<Employee> findByAgeBetween(Integer minAge, Integer maxAge);

    // Find employees by salary range
    List<Employee> findBySalaryBetween(Double minSalary, Double maxSalary);

    // Get all employees ordered by name
    @Query("SELECT e FROM Employee e ORDER BY e.name ASC")
    List<Employee> findAllOrderByName();

    // Get all employees ordered by salary desc
    @Query("SELECT e FROM Employee e ORDER BY e.salary DESC")
    List<Employee> findAllOrderBySalaryDesc();
}