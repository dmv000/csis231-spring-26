package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.DepartmentDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto savedDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid department ID provided.");
        }
        DepartmentDto departmentDto = departmentService.getDepartmentById(id);
        return new ResponseEntity<>(departmentDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departmentDtoList = departmentService.getAllDepartments();
        return new ResponseEntity<>(departmentDtoList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDto departmentDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid department ID provided.");
        }
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid department ID provided.");
        }
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
