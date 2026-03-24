package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.LoginDto;
import com.csis231.springpostgrescrud.dto.UserDto;
import com.csis231.springpostgrescrud.dto.PagedResponseDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import com.csis231.springpostgrescrud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "email");

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        UserDto savedUser = userService.registerUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user ID provided.");
        }
        UserDto userDto = userService.getUserById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userService.getAllUsers();
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<UserDto>> searchUsers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "username") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if (page < 0) {
            throw new BadRequestException("page must be >= 0");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("size must be between 1 and 100");
        }

        String normalizedSortField = sortField == null ? "username" : sortField.trim();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            throw new BadRequestException("Invalid sortField. Allowed: " + String.join(", ", ALLOWED_SORT_FIELDS));
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("sortDir must be 'asc' or 'desc'");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortField));
        Page<UserDto> resultPage = userService.searchUsers(q, pageable);

        PagedResponseDto<UserDto> response = new PagedResponseDto<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                normalizedSortField + "," + direction.name().toLowerCase()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user ID provided.");
        }
        UserDto updatedUser = userService.updateUser(id, userDto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user ID provided.");
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> authenticateUser(@RequestBody LoginDto loginDto) {
        UserDto authenticatedUser = userService.authenticateUser(loginDto);
        return ResponseEntity.ok(authenticatedUser);
    }
}
