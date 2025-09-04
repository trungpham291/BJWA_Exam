package com.aptech.employee.controller;

import com.aptech.employee.entity.Employee;
import com.aptech.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String index(Model model) {
        logger.debug("Displaying main page");

        // Always provide a new employee object for the form
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Employee());
        }

        // Add employees list
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("searchQuery", "");
        model.addAttribute("totalCount", employeeService.count());

        return "index";
    }

    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute("employee") Employee employee,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        logger.debug("Adding new employee: {}", employee);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors while adding employee: {}", bindingResult.getAllErrors());

            // Re-populate model for the view
            model.addAttribute("employee", employee);
            model.addAttribute("employees", employeeService.findAll());
            model.addAttribute("searchQuery", "");
            model.addAttribute("totalCount", employeeService.count());

            return "index";
        }

        try {
            // Check if employee name already exists
            if (employeeService.existsByName(employee.getName())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Error while creating User: Unable to create. A User with name " +
                                employee.getName() + " already exist.");
                return "redirect:/";
            }

            Employee savedEmployee = employeeService.save(employee);
            logger.info("Employee added successfully: {}", savedEmployee);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");

        } catch (Exception e) {
            logger.error("Error adding employee: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while creating user: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        logger.debug("Editing employee with id: {}", id);

        Optional<Employee> employeeOpt = employeeService.findById(id);
        if (employeeOpt.isPresent()) {
            model.addAttribute("employee", employeeOpt.get());
            model.addAttribute("employees", employeeService.findAll());
            model.addAttribute("searchQuery", "");
            model.addAttribute("totalCount", employeeService.count());
            model.addAttribute("isEdit", true);
            return "index";
        } else {
            logger.warn("Employee not found with id: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found");
            return "redirect:/";
        }
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        logger.debug("Updating employee with id: {} - {}", id, employee);

        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors while updating employee: {}", bindingResult.getAllErrors());

            // Re-populate model for the view
            model.addAttribute("employee", employee);
            model.addAttribute("employees", employeeService.findAll());
            model.addAttribute("searchQuery", "");
            model.addAttribute("totalCount", employeeService.count());
            model.addAttribute("isEdit", true);

            return "index";
        }

        try {
            // Check if employee name already exists (excluding current employee)
            if (employeeService.existsByNameAndNotId(employee.getName(), id)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Error while updating user: A User with name " +
                                employee.getName() + " already exist.");
                return "redirect:/";
            }

            Employee updatedEmployee = employeeService.update(id, employee);
            logger.info("Employee updated successfully: {}", updatedEmployee);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");

        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while updating user: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {

        logger.debug("Deleting employee with id: {}", id);

        try {
            employeeService.deleteById(id);
            logger.info("Employee deleted successfully with id: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while deleting user: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchEmployees(@RequestParam(name = "query", required = false) String query,
                                  Model model) {

        logger.debug("Searching employees with query: {}", query);

        List<Employee> employees = employeeService.searchByName(query);

        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", employees);
        model.addAttribute("searchQuery", query != null ? query : "");
        model.addAttribute("totalCount", employees.size());

        return "index";
    }

    @GetMapping("/sort-by-salary")
    public String sortBySalary(Model model) {
        logger.debug("Sorting employees by salary");

        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", employeeService.findAllOrderBySalary());
        model.addAttribute("searchQuery", "");
        model.addAttribute("totalCount", employeeService.count());
        model.addAttribute("sortedBySalary", true);

        return "index";
    }
}