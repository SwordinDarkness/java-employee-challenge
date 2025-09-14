package com.reliaquest.api.service;

import com.reliaquest.api.controller.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
    }

    @Test
    void testCreateEmployee() {
        EmployeeDTO dto = new EmployeeDTO("Alice", 5000);

        Employee created = employeeService.createEmployee(dto);

        assertNotNull(created.getId(), "Employee ID should not be null");
        assertEquals("Alice", created.getName());
        assertEquals(5000, created.getSalary());

        Optional<Employee> found = employeeService.getEmployeeId(created.getId().toString());
        assertTrue(found.isPresent(), "Employee should be found in service after creation");
    }

    @Test
    void testGetAllEmployees() {
        employeeService.createEmployee(new EmployeeDTO("Alice", 5000));
        employeeService.createEmployee(new EmployeeDTO("Bob", 6000));

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(2, employees.size());
    }

    @Test
    void testSearchEmployees() {
        employeeService.createEmployee(new EmployeeDTO("Alice", 5000));
        employeeService.createEmployee(new EmployeeDTO("Bob", 6000));
        employeeService.createEmployee(new EmployeeDTO("Charlie", 7000));

        List<Employee> result = employeeService.searchEmployees("bo");

        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getName());
    }

    @Test
    void testGetEmployeeById() {
        Employee created = employeeService.createEmployee(new EmployeeDTO("Alice", 5000));

        Optional<Employee> found = employeeService.getEmployeeId(created.getId().toString());

        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        Optional<Employee> found = employeeService.getEmployeeId(UUID.randomUUID().toString());

        assertTrue(found.isEmpty(), "Should return empty Optional when ID does not exist");
    }

    @Test
    void testGetHighestSalary() {
        employeeService.createEmployee(new EmployeeDTO("Alice", 5000));
        employeeService.createEmployee(new EmployeeDTO("Bob", 7000));
        employeeService.createEmployee(new EmployeeDTO("Charlie", 6000));

        int highest = employeeService.getHighestSalary();

        assertEquals(7000, highest);
    }

    @Test
    void testGetTopTenHighestSalaries() {
        employeeService.createEmployee(new EmployeeDTO("Alice", 1000));
        employeeService.createEmployee(new EmployeeDTO("Bob", 2000));
        employeeService.createEmployee(new EmployeeDTO("Charlie", 3000));
        employeeService.createEmployee(new EmployeeDTO("David", 4000));
        employeeService.createEmployee(new EmployeeDTO("Eve", 5000));
        employeeService.createEmployee(new EmployeeDTO("Frank", 6000));
        employeeService.createEmployee(new EmployeeDTO("Grace", 7000));
        employeeService.createEmployee(new EmployeeDTO("Heidi", 8000));
        employeeService.createEmployee(new EmployeeDTO("Ivan", 9000));
        employeeService.createEmployee(new EmployeeDTO("Judy", 10000));
        employeeService.createEmployee(new EmployeeDTO("Mallory", 11000)); // should not appear

        List<String> topTen = employeeService.getTopTenHighestSalaries();

        assertEquals(10, topTen.size());
        assertEquals("Mallory", topTen.get(0), "Highest salary should be first");
        assertFalse(topTen.contains("Alice"), "Lowest salary should be excluded");
    }

    @Test
    void testDeleteEmployeeById() {
        Employee created = employeeService.createEmployee(new EmployeeDTO("Alice", 5000));

        employeeService.deleteEmployeeById(created.getId().toString());

        Optional<Employee> found = employeeService.getEmployeeId(created.getId().toString());
        assertTrue(found.isEmpty(), "Employee should be removed from the service");
    }

    @Test
    void testDeleteEmployeeById_NotFound() {
        String randomId = UUID.randomUUID().toString();

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployeeById(randomId),
                "Should throw exception if employee does not exist");
    }

}
