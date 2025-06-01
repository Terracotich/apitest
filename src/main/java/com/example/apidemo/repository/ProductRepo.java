package com.example.apidemo.repository;

import com.example.apidemo.dto.ProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<ProductDto, Long> {
    List<ProductDto> findByProductTitleContainingIgnoreCase(String productTitle);
    List<ProductDto> findByBrand_Id(Long brandId);
    List<ProductDto> findByCategory_Id(Long categoryId);

    @Query("SELECT p FROM ProductDto p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<ProductDto> findByPriceRange(@Param("minPrice") Integer minPrice,
                                      @Param("maxPrice") Integer maxPrice);

    boolean existsByProductTitle(String productTitle);
}