package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.DepartmentDto;
import com.csis231.springpostgrescrud.entity.Department;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.DepartmentMapper;
import com.csis231.springpostgrescrud.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private DepartmentRepository departmentRepository;

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = DepartmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return DepartmentMapper.toDto(savedDepartment);
    }

    @Override
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with given id: " + id));
        return DepartmentMapper.toDto(department);
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with given id: " + id));
        department.setName(departmentDto.getName());
        department.setLocation(departmentDto.getLocation());
        Department updatedDepartment = departmentRepository.save(department);
        return DepartmentMapper.toDto(updatedDepartment);
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with given id: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
