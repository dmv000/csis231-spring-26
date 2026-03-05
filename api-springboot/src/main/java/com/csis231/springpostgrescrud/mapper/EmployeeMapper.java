package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.EmployeeDto;
import com.csis231.springpostgrescrud.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto toDto(Employee employee) {
        Long departmentId = employee.getDepartment() != null ? employee.getDepartment().getId() : null;
        String departmentName = employee.getDepartment() != null ? employee.getDepartment().getName() : null;
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                departmentId,
                departmentName
        );
    }

    public static Employee toEntity(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setId(employeeDto.getId());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        return employee;
    }
}
