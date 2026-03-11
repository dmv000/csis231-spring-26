package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}