package com.example.apidemo.controller;

import com.example.apidemo.dto.UserDto;
import com.example.apidemo.exception.ResourceNotFoundException;
import com.example.apidemo.repository.UserRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserRepo repository;

    @Autowired
    public UserController(UserRepo repository) {
        this.repository = repository;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        // Явно сбрасываем ID и version
        userDto.setId(null);
        userDto.setVersion(0);

        // Если key должен генерироваться автоматически
        userDto.setKey(null);

        if (repository.existsByPhoneNumber(userDto.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserDto savedUser = repository.save(userDto);
        return ResponseEntity.ok(savedUser);
    }

    @Operation(summary = "Update user information", description = "Updates existing user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true)
            @Valid @RequestBody UserDto userDto) {
        return repository.findById(id)
                .map(existingUser -> {
                    userDto.setKey(id);
                    return ResponseEntity.ok(repository.save(userDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Operation(summary = "Get user by ID", description = "Returns a single user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable Long id) {
        UserDto user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponse(responseCode = "200", description = "List of all users",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class))})
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(repository.findAll());
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}