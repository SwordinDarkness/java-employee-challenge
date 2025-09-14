package com.reliaquest.server.service;

import com.reliaquest.server.config.ServerConfiguration;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockEmployeeService {

    private final Faker faker;

    @Getter
    private final List<MockEmployee> mockEmployees;

    public Optional<MockEmployee> findById(@NonNull UUID uuid) {
        return mockEmployees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getId())
                        && mockEmployee.getId().equals(uuid))
                .findFirst();
    }

    public MockEmployee create(@NonNull CreateMockEmployeeInput input) {
        final var mockEmployee = MockEmployee.from(
                ServerConfiguration.EMAIL_TEMPLATE.formatted(
                        faker.twitter().userName().toLowerCase()),
                input);
        mockEmployees.add(mockEmployee);
        log.debug("Added employee: {}", mockEmployee);
        return mockEmployee;
    }

    public boolean delete(@NonNull String id) {
        final var mockEmployee = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getId())
                        && employee.getId().toString().equalsIgnoreCase( id))
                .findFirst();
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return true;
        }

        return false;
    }

    /**Added methods to the MockEmployeeService to search for employees based on a search String
     *
     *
     * @param search
     * @return List of Employees based on search String
     */
    public List<MockEmployee> searchEmployees(String search) {
        return mockEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }
    /**Added method to the MockEmployeeService to return the highest salary
     *
     *
     *
     * @return int of
     */

    public int getHighestSalary() {
        return mockEmployees.stream().mapToInt(MockEmployee::getSalary).max().orElse(0);
    }
    /**Added method to the MockEmployeeService to return the Top ten salaries
     *
     *
     *
     * @return List of names of the 10 employees with the
     */
    public List<String> getTopTenHighestSalaries() {
        return mockEmployees.stream()
                .sorted(Comparator.comparingInt(MockEmployee::getSalary).reversed())
                .limit(10)
                .map(MockEmployee::getName)
                .collect(Collectors.toList());
    }
}
