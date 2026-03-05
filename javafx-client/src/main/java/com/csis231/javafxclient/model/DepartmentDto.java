package com.csis231.javafxclient.model;

import java.util.List;

public class DepartmentDto {
    private Long id;
    private String name;
    private String location;
    private List<EmployeeDto> employees;

    public DepartmentDto() {
    }

    public DepartmentDto(Long id, String name, String location, List<EmployeeDto> employees) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.employees = employees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<EmployeeDto> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDto> employees) {
        this.employees = employees;
    }
}
