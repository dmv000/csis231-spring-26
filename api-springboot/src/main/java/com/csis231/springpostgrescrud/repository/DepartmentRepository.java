package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
