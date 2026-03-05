package com.csis231.springpostgrescrud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
    private Long id;
    private String name;
    private String location;
    private List<EmployeeDto> employees;
}
