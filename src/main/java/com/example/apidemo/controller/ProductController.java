package com.example.apidemo.controller;

import com.example.apidemo.dto.*;
import com.example.apidemo.exception.ResourceNotFoundException;
import com.example.apidemo.repository.*;
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
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "Endpoints for managing products")
public class ProductController {

    private final ProductRepo productRepository;
    private final BrandRepo brandRepository;
    private final CategoryRepo categoryRepository;

    @Autowired
    public ProductController(ProductRepo productRepository,
                             BrandRepo brandRepository,
                             CategoryRepo categoryRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Brand or Category not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Product already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Parameter(description = "Product data to create", required = true)
            @Valid @RequestBody ProductDto productDto) {
        productDto.setId(null);
        productDto.setVersion(0);

        // Проверка существования бренда и категории
        BrandDto brand = brandRepository.findById(productDto.getBrand().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + productDto.getBrand().getId()));

        CategoryDto category = categoryRepository.findById(productDto.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategory().getId()));

        productDto.setBrand(brand);
        productDto.setCategory(category);

        if (productRepository.existsByProductTitle(productDto.getProductTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ProductDto savedProduct = productRepository.save(productDto);
        return ResponseEntity.ok(savedProduct);
    }

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponse(responseCode = "200", description = "List of all products",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDto.class))})
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @Operation(summary = "Get product by ID", description = "Returns a single product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true)
            @PathVariable Long id) {
        ProductDto product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update product", description = "Updates existing product information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product, Brand or Category not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "ID of the product to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product data", required = true)
            @Valid @RequestBody ProductDto productDto) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Проверка существования бренда и категории
                    BrandDto brand = brandRepository.findById(productDto.getBrand().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + productDto.getBrand().getId()));

                    CategoryDto category = categoryRepository.findById(productDto.getCategory().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategory().getId()));

                    productDto.setId(id);
                    productDto.setBrand(brand);
                    productDto.setCategory(category);

                    return ResponseEntity.ok(productRepository.save(productDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Operation(summary = "Delete product", description = "Deletes a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true)
            @PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search products by title", description = "Returns products containing the search string in title")
    @ApiResponse(responseCode = "200", description = "List of matching products",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDto.class))})
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @Parameter(description = "Search string for product title", required = true)
            @RequestParam String title) {
        return ResponseEntity.ok(productRepository.findByProductTitleContainingIgnoreCase(title));
    }

    @Operation(summary = "Filter products by brand", description = "Returns products of specified brand")
    @ApiResponse(responseCode = "200", description = "List of products by brand",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDto.class))})
    @GetMapping("/by-brand/{brandId}")
    public ResponseEntity<List<ProductDto>> getProductsByBrand(
            @Parameter(description = "ID of the brand", required = true)
            @PathVariable Long brandId) {
        return ResponseEntity.ok(productRepository.findByBrand_Id(brandId));
    }

    @Operation(summary = "Filter products by category", description = "Returns products of specified category")
    @ApiResponse(responseCode = "200", description = "List of products by category",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDto.class))})
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @Parameter(description = "ID of the category", required = true)
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(productRepository.findByCategory_Id(categoryId));
    }

    @Operation(summary = "Filter products by price range",
            description = "Returns products with price between min and max values")
    @ApiResponse(responseCode = "200", description = "List of products in price range",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDto.class))})
    @GetMapping("/by-price")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(
            @Parameter(description = "Minimum price", required = true)
            @RequestParam Integer minPrice,
            @Parameter(description = "Maximum price", required = true)
            @RequestParam Integer maxPrice) {
        return ResponseEntity.ok(productRepository.findByPriceRange(minPrice, maxPrice));
    }
}