package com.example.apidemo.repository;

import com.example.apidemo.dto.OrderDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepo extends JpaRepository<OrderDto, Long> {

    // Используем правильное имя поля из OrderDto (userId)
    List<OrderDto> findByUserId(Long userId);

    List<OrderDto> findByStatus(String status);

    @Query("SELECT o FROM OrderDto o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<OrderDto> findByOrderDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}