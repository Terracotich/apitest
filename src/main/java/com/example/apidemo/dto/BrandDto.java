package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "brand")
public class BrandDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    @NotBlank(message = "Brand title is required")
    @Size(max = 30, message = "Brand title must be less than 30 characters")
    @Column(name = "brandtitle", nullable = false)
    private String brandTitle;

    // Конструкторы
    public BrandDto() {
    }

    public BrandDto(String brandTitle) {
        this.brandTitle = brandTitle;
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

    public String getBrandTitle() {
        return brandTitle;
    }

    public void setBrandTitle(String brandTitle) {
        this.brandTitle = brandTitle;
    }
}