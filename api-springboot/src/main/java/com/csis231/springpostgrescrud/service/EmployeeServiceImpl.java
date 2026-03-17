package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.EmployeeDto;
import com.csis231.springpostgrescrud.entity.Department;
import com.csis231.springpostgrescrud.entity.Employee;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.EmployeeMapper;
import com.csis231.springpostgrescrud.repository.DepartmentRepository;
import com.csis231.springpostgrescrud.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        Employee employee = EmployeeMapper.toEntity(employeeDto);
        if (employeeDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDto.getDepartmentId()));
            employee.setDepartment(department);
        }
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                // PROPAGATE ERROR.
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found with given id: "+id));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map((employee)-> EmployeeMapper.toDto(employee))
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDto> searchEmployees(String q, Pageable pageable) {
        String query = q == null ? "" : q.trim();
        if (query.isEmpty()) {
            return employeeRepository.findAll(pageable).map(EmployeeMapper::toDto);
        }
        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, pageable
                )
                .map(EmployeeMapper::toDto);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with given id: " + id));
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        if (employeeDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDto.getDepartmentId()));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }
        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(updatedEmployee);
    }
}
