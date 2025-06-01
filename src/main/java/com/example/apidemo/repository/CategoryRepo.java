package com.example.apidemo.repository;

import com.example.apidemo.dto.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<CategoryDto, Long> {
    CategoryDto findByCategoryTitle(String categoryTitle);
    boolean existsByCategoryTitle(String categoryTitle);
}