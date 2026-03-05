package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
