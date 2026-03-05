package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.DepartmentDto;
import com.csis231.springpostgrescrud.entity.Department;

import java.util.Collections;
import java.util.stream.Collectors;

public class DepartmentMapper {

    public static DepartmentDto toDto(Department department) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getLocation(),
                department.getEmployees() != null
                        ? department.getEmployees().stream()
                                .map(EmployeeMapper::toDto)
                                .collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }

    public static DepartmentDto toDtoWithoutEmployees(Department department) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getLocation(),
                null
        );
    }

    public static Department toEntity(DepartmentDto departmentDto) {
        Department department = new Department();
        department.setId(departmentDto.getId());
        department.setName(departmentDto.getName());
        department.setLocation(departmentDto.getLocation());
        return department;
    }
}
