package com.example.apidemo.controller;

import com.example.apidemo.dto.CategoryDto;
import com.example.apidemo.exception.ResourceNotFoundException;
import com.example.apidemo.repository.CategoryRepo;
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
@RequestMapping("/api/categories")
@Tag(name = "Category Management", description = "Endpoints for managing product categories")
public class CategoryController {

    private final CategoryRepo repository;

    @Autowired
    public CategoryController(CategoryRepo repository) {
        this.repository = repository;
    }

    @Operation(summary = "Create a new category", description = "Creates a new product category in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Category already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Parameter(description = "Category data to create", required = true)
            @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(null);
        categoryDto.setVersion(0);

        if (repository.existsByCategoryTitle(categoryDto.getCategoryTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        CategoryDto savedCategory = repository.save(categoryDto);
        return ResponseEntity.ok(savedCategory);
    }

    @Operation(summary = "Get all categories", description = "Returns a list of all product categories")
    @ApiResponse(responseCode = "200", description = "List of all categories",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDto.class))})
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(repository.findAll());
    }

    @Operation(summary = "Get category by ID", description = "Returns a single category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class))}),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(
            @Parameter(description = "ID of the category to retrieve", required = true)
            @PathVariable Long id) {
        CategoryDto category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Update category", description = "Updates existing category information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @Parameter(description = "ID of the category to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated category data", required = true)
            @Valid @RequestBody CategoryDto categoryDto) {
        return repository.findById(id)
                .map(existingCategory -> {
                    categoryDto.setId(id);
                    return ResponseEntity.ok(repository.save(categoryDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Operation(summary = "Delete category", description = "Deletes a category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true)
            @PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}