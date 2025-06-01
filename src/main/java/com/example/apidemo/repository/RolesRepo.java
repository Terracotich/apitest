package com.example.apidemo.repository;

import com.example.apidemo.dto.RolesDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepo extends JpaRepository<RolesDto, Long> {
    RolesDto findByCharacterTitle(String characterTitle);
    boolean existsByCharacterTitle(String characterTitle);
}