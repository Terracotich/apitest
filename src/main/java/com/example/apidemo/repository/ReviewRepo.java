package com.example.apidemo.repository;

import com.example.apidemo.dto.ReviewDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepo extends JpaRepository<ReviewDto, Long> {
    List<ReviewDto> findByUserId(Long userId);
    List<ReviewDto> findByOrderId(Long orderId);

    @Query("SELECT r FROM ReviewDto r WHERE r.rating >= :minRating")
    List<ReviewDto> findByMinRating(@Param("minRating") Integer minRating);

    boolean existsByUserIdAndOrderId(Long userId, Long orderId);
}