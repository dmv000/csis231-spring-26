package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.UserDto;
import com.csis231.springpostgrescrud.dto.LoginDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    Page<UserDto> searchUsers(String q, Pageable pageable);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto authenticateUser(LoginDto loginDto);
}
