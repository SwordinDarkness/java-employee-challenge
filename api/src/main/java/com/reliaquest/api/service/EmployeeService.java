package com.reliaquest.api.service;

import com.reliaquest.api.controller.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
/**
 * Service class for managing Employee objects.
 * This class provides functionality to create, search, retrieve, and delete employees,
 * as well as retrieve information like highest salaries.
 */

@Service
public class EmployeeService {

    /**
     * In-memory storage for employees.
     * The key is a String-based ID, and the value is the corresponding Employee object.
     */
    private final Map<String, Employee> employeeMap = new HashMap<>();

    /**
     * Retrieves all employees in the system.
     *
     * @return a list of all {@link Employee} objects currently stored
     */
    public List<Employee> getAllEmployees() {
        // Return a new ArrayList to avoid exposing the internal map values directly
        return new ArrayList<>(employeeMap.values());
    }

    /**
     * Searches employees by their name.
     * The search is case-insensitive and matches substrings.
     *
     * @param search the search keyword to look for in employee names
     * @return a list of employees whose names contain the search term
     */
    public List<Employee> searchEmployees(String search) {
        return employeeMap.values().stream()
                // Convert both strings to lowercase for case-insensitive matching
                .filter(employee -> employee.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an employee by their unique identifier.
     *
     * @param id the ID of the employee
     * @return an {@link Optional} containing the employee if found, or empty if not found
     */
    public Optional<Employee> getEmployeeId(String id) {
        // Lookup employee by ID, wrap result in Optional
        return Optional.ofNullable(employeeMap.get(id));
    }

    /**
     * Finds the highest salary among all employees.
     *
     * @return the maximum salary value, or 0 if no employees exist
     */
    public int getHighestSalary() {
        return employeeMap.values().stream()
                .mapToInt(Employee::getSalary) // Convert employees to salary values
                .max()                          // Find the maximum salary
                .orElse(0);                     // Return 0 if no employees are present
    }

    /**
     * Retrieves the names of the top 10 highest-paid employees.
     * The list is sorted in descending order of salary.
     *
     * @return a list of employee names with the top 10 highest salaries
     */
    public List<String> getTopTenHighestSalaries() {
        return employeeMap.values().stream()
                // Sort employees by salary in descending order
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10) // Limit to the top 10 employees
                .map(Employee::getName) // Extract just the employee names
                .collect(Collectors.toList());
    }

    /**
     * Creates a new employee from the provided {@link EmployeeDTO}.
     * Generates a new unique ID for the employee and uses it both for the map key
     * and for the Employee object itself to ensure consistency.
     *
     * @param employeeDTO the data transfer object containing employee details
     * @return the created {@link Employee} object
     */
    public Employee createEmployee(EmployeeDTO employeeDTO) {
        // Generate a single UUID for both the employee object and the map key
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        // Create a new Employee instance with the generated UUID
        Employee employee = new Employee(uuid, employeeDTO.getName(), employeeDTO.getSalary());

        // Store the employee in the map using the same ID
        employeeMap.put(id, employee);

        return employee;
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete
     * @throws EmployeeNotFoundException if no employee with the given ID exists
     */
    public void deleteEmployeeById(String id) {
        // Retrieve the employee, or throw an exception if not found
        Employee employee = this.getEmployeeId(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Remove the employee directly using the provided ID (which matches map key)
        employeeMap.remove(id);
    }
}
