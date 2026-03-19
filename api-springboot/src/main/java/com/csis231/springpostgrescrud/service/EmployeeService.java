package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.EmployeeDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);
    EmployeeDto getEmployeeById(Long id);
    List<EmployeeDto> getAllEmployees();
    Page<EmployeeDto> searchEmployees(String q, Pageable pageable);
    EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto);
    void seedEmployees(int num);
}
