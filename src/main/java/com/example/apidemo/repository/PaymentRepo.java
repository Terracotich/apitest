package com.example.apidemo.repository;

import com.example.apidemo.dto.PaymentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepo extends JpaRepository<PaymentDto, Long> {
    List<PaymentDto> findByUserId(Long userId);
    List<PaymentDto> findByOrderId(Long orderId);

    @Query("SELECT p FROM PaymentDto p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<PaymentDto> findByPaymentDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p FROM PaymentDto p WHERE p.paymentMethod = :method")
    List<PaymentDto> findByPaymentMethod(@Param("method") String method);
}