package com.example.apidemo.controller;

import com.example.apidemo.dto.RolesDto;
import com.example.apidemo.exception.ResourceNotFoundException;
import com.example.apidemo.repository.RolesRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "Endpoints for managing roles")
public class RolesController {

    private final RolesRepo repository;

    @Autowired
    public RolesController(RolesRepo repository) {
        this.repository = repository;
    }

    @Operation(summary = "Create a new role", description = "Creates a new role in the system")
    @ApiResponse(responseCode = "200", description = "Role created successfully")
    @PostMapping
    public ResponseEntity<RolesDto> createRole(
            @Parameter(description = "Role data to create", required = true)
            @Valid @RequestBody RolesDto rolesDto) {
        // Убедимся, что ID действительно null
        rolesDto.setId(null);
        rolesDto.setVersion(0);

        if (repository.existsByCharacterTitle(rolesDto.getCharacterTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        RolesDto savedRole = repository.save(rolesDto);
        return ResponseEntity.ok(savedRole);
    }

    @Operation(summary = "Get all roles", description = "Returns a list of all roles")
    @GetMapping
    public ResponseEntity<List<RolesDto>> getAllRoles() {
        return ResponseEntity.ok(repository.findAll());
    }

    @Operation(summary = "Get role by ID", description = "Returns a single role by ID")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @GetMapping("/{id}")
    public ResponseEntity<RolesDto> getRoleById(
            @Parameter(description = "ID of the role to retrieve", required = true)
            @PathVariable Long id) {
        RolesDto role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Update role", description = "Updates existing role information")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @PutMapping("/{id}")
    public ResponseEntity<RolesDto> updateRole(
            @Parameter(description = "ID of the role to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated role data", required = true)
            @Valid @RequestBody RolesDto rolesDto) {
        return repository.findById(id)
                .map(existingRole -> {
                    rolesDto.setId(id);
                    return ResponseEntity.ok(repository.save(rolesDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Operation(summary = "Delete role", description = "Deletes a role by ID")
    @ApiResponse(responseCode = "204", description = "Role deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID of the role to delete", required = true)
            @PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}