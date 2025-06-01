package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "product")
public class ProductDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    @NotBlank(message = "Product title is required")
    @Size(max = 30, message = "Product title must be less than 30 characters")
    @Column(name = "producttitle", nullable = false)
    private String productTitle;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Brand ID is required")
    @ManyToOne
    @JoinColumn(name = "brandid", nullable = false)
    private BrandDto brand;

    @NotNull(message = "Category ID is required")
    @ManyToOne
    @JoinColumn(name = "categoryid", nullable = false)
    private CategoryDto category;

    // Конструкторы
    public ProductDto() {
    }

    public ProductDto(String productTitle, Integer price, Integer quantity,
                      BrandDto brand, CategoryDto category) {
        this.productTitle = productTitle;
        this.price = price;
        this.quantity = quantity;
        this.brand = brand;
        this.category = category;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BrandDto getBrand() {
        return brand;
    }

    public void setBrand(BrandDto brand) {
        this.brand = brand;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }
}