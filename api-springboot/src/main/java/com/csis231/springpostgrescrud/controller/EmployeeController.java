package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.EmployeeDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        EmployeeDto saveEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(saveEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employee ID provided.");
        }
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employeeDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employeeDtoList = employeeService.getAllEmployees();
        return new ResponseEntity<>(employeeDtoList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employee ID provided.");
        }
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }
}
