package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllEmployees() throws Exception {
        Employee e1 = new Employee(UUID.randomUUID(), "Alice", 5000);
        Employee e2 = new Employee(UUID.randomUUID(), "Bob", 6000);

        when(service.getAllEmployees()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void testSearchEmployees() throws Exception {
        Employee e1 = new Employee(UUID.randomUUID(), "Alice", 5000);

        when(service.searchEmployees("alice")).thenReturn(List.of(e1));

        mockMvc.perform(get("/api/v1/employee/search/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void testGetEmployeeById_Found() throws Exception {
        UUID id = UUID.randomUUID();
        Employee e1 = new Employee(id, "Alice", 5000);

        when(service.getEmployeeId(id.toString())).thenReturn(Optional.of(e1));

        mockMvc.perform(get("/api/v1/employee/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.salary").value(5000));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(service.getEmployeeId(id.toString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employee/{id}", id.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHighestSalary() throws Exception {
        when(service.getHighestSalary()).thenReturn(7000);

        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("7000"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        when(service.getTopTenHighestSalaries()).thenReturn(Arrays.asList("Alice", "Bob"));

        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0]").value("Alice"))
                .andExpect(jsonPath("$[1]").value("Bob"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeDTO dto = new EmployeeDTO("Alice", 5000);
        Employee created = new Employee(UUID.randomUUID(), "Alice", 5000);

        when(service.createEmployee(dto)).thenReturn(created);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteEmployeeById() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(service).deleteEmployeeById(id.toString());

        mockMvc.perform(delete("/api/v1/employee/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee with id " + id + " was deleted"));
    }
}
