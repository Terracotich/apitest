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
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderRepo orderRepository;
    private final UserRepo userRepository;

    @Autowired
    public OrderController(OrderRepo orderRepository, UserRepo userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Create a new order", description = "Creates a new order in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto) {
        orderDto.setId(null);
        orderDto.setVersion(0);

        if (orderDto.getUserId() == null || orderDto.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        if (!userRepository.existsById(orderDto.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + orderDto.getUserId());
        }

        OrderDto savedOrder = orderRepository.save(orderDto);
        return ResponseEntity.ok(savedOrder);
    }

    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponse(responseCode = "200", description = "List of all orders",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDto.class))})
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @Operation(summary = "Get order by ID", description = "Returns a single order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id) {
        OrderDto order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Update order", description = "Updates existing order information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order or User not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated order data", required = true)
            @Valid @RequestBody OrderDto orderDto) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    // Проверка существования пользователя
                    if (!userRepository.existsById(orderDto.getUserId())) {
                        throw new ResourceNotFoundException("User not found with id: " + orderDto.getUserId());
                    }

                    orderDto.setId(id);
                    return ResponseEntity.ok(orderRepository.save(orderDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Operation(summary = "Delete order", description = "Deletes an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the order to delete", required = true)
            @PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get orders by user", description = "Returns all orders for a specific user")
    @ApiResponse(responseCode = "200", description = "List of user's orders",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDto.class))})
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }

    @Operation(summary = "Get orders by status", description = "Returns all orders with specific status")
    @ApiResponse(responseCode = "200", description = "List of orders with status",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDto.class))})
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(
            @Parameter(description = "Status to filter by", required = true)
            @PathVariable String status) {
        return ResponseEntity.ok(orderRepository.findByStatus(status));
    }

    @Operation(summary = "Get orders by date range",
            description = "Returns all orders between start and end dates")
    @ApiResponse(responseCode = "200", description = "List of orders in date range",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDto.class))})
    @GetMapping("/by-date")
    public ResponseEntity<List<OrderDto>> getOrdersByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam String startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(orderRepository.findByOrderDateBetween(start, end));
    }
}