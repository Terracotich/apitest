package com.example.apidemo.repository;

import com.example.apidemo.dto.BrandDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepo extends JpaRepository<BrandDto, Long> {
    BrandDto findByBrandTitle(String brandTitle);
    boolean existsByBrandTitle(String brandTitle);
}