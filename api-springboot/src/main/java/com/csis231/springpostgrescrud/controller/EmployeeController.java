package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.EmployeeDto;
import com.csis231.springpostgrescrud.dto.PagedResponseDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private EmployeeService employeeService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "firstName", "lastName", "email");

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

    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<EmployeeDto>> searchEmployees(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "lastName") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if (page < 0) {
            throw new BadRequestException("page must be >= 0");
        }
        if (size < 1 || size > 1000) {
            throw new BadRequestException("size must be between 1 and 100");
        }

        String normalizedSortField = sortField == null ? "lastName" : sortField.trim();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            throw new BadRequestException("Invalid sortField. Allowed: " + String.join(", ", ALLOWED_SORT_FIELDS));
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("sortDir must be 'asc' or 'desc'");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortField));
        Page<EmployeeDto> resultPage = employeeService.searchEmployees(q, pageable);

        PagedResponseDto<EmployeeDto> response = new PagedResponseDto<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                normalizedSortField + "," + direction.name().toLowerCase()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employee ID provided.");
        }
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @GetMapping("/seedEmps/{num}")
    public ResponseEntity<String> seedEmps(@PathVariable int num) {
        if ( num <= 0) {
            throw new BadRequestException("Invalid seeding num");
        }
        employeeService.seedEmployees(num);
        return new ResponseEntity<>("Seeding completed", HttpStatus.OK);
    }
}
