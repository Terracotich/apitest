package com.example.apidemo.controller;

import com.example.apidemo.dto.*;
import com.example.apidemo.exception.ResourceNotFoundException;
import com.example.apidemo.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "Endpoints for managing payments")
public class PaymentController {

    private final PaymentRepo paymentRepository;
    private final UserRepo userRepository;
    private final OrderRepo orderRepository;

    @Autowired
    public PaymentController(PaymentRepo paymentRepository,
                             UserRepo userRepository,
                             OrderRepo orderRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Operation(summary = "Create a new payment", description = "Creates a new payment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User or Order not found",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(
            @Parameter(description = "Payment data to create", required = true)
            @Valid @RequestBody PaymentDto paymentDto) {
        paymentDto.setId(null);
        paymentDto.setVersion(0);

        // Проверка существования пользователя и заказа
        if (!userRepository.existsById(paymentDto.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + paymentDto.getUserId());
        }

        if (!orderRepository.existsById(paymentDto.getOrderId())) {
            throw new ResourceNotFoundException("Order not found with id: " + paymentDto.getOrderId());
        }

        PaymentDto savedPayment = paymentRepository.save(paymentDto);
        return ResponseEntity.ok(savedPayment);
    }

    @Operation(summary = "Get all payments", description = "Returns a list of all payments")
    @ApiResponse(responseCode = "200", description = "List of all payments",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaymentDto.class))})
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @Operation(summary = "Get payment by ID", description = "Returns a single payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDto.class))}),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(
            @Parameter(description = "ID of the payment to retrieve", required = true)
            @PathVariable Long id) {
        PaymentDto payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "Update payment", description = "Updates existing payment information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment, User or Order not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(
            @Parameter(description = "ID of the payment to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated payment data", required = true)
            @Valid @RequestBody PaymentDto paymentDto) {
        return paymentRepository.findById(id)
                .map(existingPayment -> {
                    // Проверка существования пользователя и заказа
                    if (!userRepository.existsById(paymentDto.getUserId())) {
                        throw new ResourceNotFoundException("User not found with id: " + paymentDto.getUserId());
                    }

                    if (!orderRepository.existsById(paymentDto.getOrderId())) {
                        throw new ResourceNotFoundException("Order not found with id: " + paymentDto.getOrderId());
                    }

                    paymentDto.setId(id);
                    return ResponseEntity.ok(paymentRepository.save(paymentDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    @Operation(summary = "Delete payment", description = "Deletes a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "ID of the payment to delete", required = true)
            @PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get payments by user", description = "Returns all payments by a specific user")
    @ApiResponse(responseCode = "200", description = "List of user's payments",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaymentDto.class))})
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByUser(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(paymentRepository.findByUserId(userId));
    }

    @Operation(summary = "Get payments by order", description = "Returns all payments for a specific order")
    @ApiResponse(responseCode = "200", description = "List of payments for the order",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaymentDto.class))})
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByOrder(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentRepository.findByOrderId(orderId));
    }

    @Operation(summary = "Get payments by date range",
            description = "Returns all payments between start and end dates")
    @ApiResponse(responseCode = "200", description = "List of payments in date range",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaymentDto.class))})
    @GetMapping("/by-date")
    public ResponseEntity<List<PaymentDto>> getPaymentsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(paymentRepository.findByPaymentDateBetween(start, end));
    }

    @Operation(summary = "Get payments by method",
            description = "Returns all payments with specified payment method")
    @ApiResponse(responseCode = "200", description = "List of payments with method",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaymentDto.class))})
    @GetMapping("/by-method/{method}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByMethod(
            @Parameter(description = "Payment method to filter by", required = true)
            @PathVariable String method) {
        return ResponseEntity.ok(paymentRepository.findByPaymentMethod(method));
    }
}