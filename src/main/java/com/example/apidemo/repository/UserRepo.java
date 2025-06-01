package com.example.apidemo.repository;

import com.example.apidemo.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserDto, Long> {
    Optional<UserDto> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByClientLogin(String clientLogin);
    List<UserDto> findByRoleId(Integer roleId);
}