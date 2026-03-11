package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.UserDto;
import com.csis231.springpostgrescrud.entity.User;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.UserMapper;
import com.csis231.springpostgrescrud.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto registerUser(UserDto userDto) {
        System.out.println("Service: attempting to register user " + userDto.getUsername());
        User user = UserMapper.toEntity(userDto);
        System.out.println("Service: mapped DTO to entity with username=" + user.getUsername());
        User savedUser = userRepository.save(user);
        System.out.println("Service: repository returned entity with id=" + savedUser.getId());
        return UserMapper.toDto(savedUser);
    }


    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given id: " + id));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given id: " + id));

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(userDto.getPassword());
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with given id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username) &&
                        user.getPassword().equals(password));
    }
}