package com.example.apidemo.controller;

import com.example.apidemo.dto.BrandDto;
import com.example.apidemo.exception.ResourceNotFoundException;
import com.example.apidemo.repository.BrandRepo;
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
@RequestMapping("/api/brands")
@Tag(name = "Brand Management", description = "Endpoints for managing brands")
public class BrandController {

    private final BrandRepo repository;

    @Autowired
    public BrandController(BrandRepo repository) {
        this.repository = repository;
    }

    @Operation(summary = "Create a new brand", description = "Creates a new brand in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BrandDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Brand already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(
            @Parameter(description = "Brand data to create", required = true)
            @Valid @RequestBody BrandDto brandDto) {
        brandDto.setId(null);
        brandDto.setVersion(0);

        if (repository.existsByBrandTitle(brandDto.getBrandTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        BrandDto savedBrand = repository.save(brandDto);
        return ResponseEntity.ok(savedBrand);
    }

    @Operation(summary = "Get all brands", description = "Returns a list of all brands")
    @ApiResponse(responseCode = "200", description = "List of all brands",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BrandDto.class))})
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(repository.findAll());
    }

    @Operation(summary = "Get brand by ID", description = "Returns a single brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BrandDto.class))}),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(
            @Parameter(description = "ID of the brand to retrieve", required = true)
            @PathVariable Long id) {
        BrandDto brand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return ResponseEntity.ok(brand);
    }

    @Operation(summary = "Update brand", description = "Updates existing brand information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(
            @Parameter(description = "ID of the brand to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated brand data", required = true)
            @Valid @RequestBody BrandDto brandDto) {
        return repository.findById(id)
                .map(existingBrand -> {
                    brandDto.setId(id);
                    return ResponseEntity.ok(repository.save(brandDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
    }

    @Operation(summary = "Delete brand", description = "Deletes a brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(
            @Parameter(description = "ID of the brand to delete", required = true)
            @PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found with id: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}