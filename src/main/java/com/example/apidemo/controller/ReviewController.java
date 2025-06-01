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
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "Endpoints for managing product reviews")
public class ReviewController {

    private final ReviewRepo reviewRepository;
    private final UserRepo userRepository;
    private final OrderRepo orderRepository;

    @Autowired
    public ReviewController(ReviewRepo reviewRepository,
                            UserRepo userRepository,
                            OrderRepo orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Operation(summary = "Create a new review", description = "Creates a new product review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User or Order not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Review already exists for this order",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @Parameter(description = "Review data to create", required = true)
            @Valid @RequestBody ReviewDto reviewDto) {
        reviewDto.setId(null);
        reviewDto.setVersion(0);

        // Проверка существования пользователя и заказа
        if (!userRepository.existsById(reviewDto.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + reviewDto.getUserId());
        }

        if (!orderRepository.existsById(reviewDto.getOrderId())) {
            throw new ResourceNotFoundException("Order not found with id: " + reviewDto.getOrderId());
        }

        // Проверка, что отзыв для этого заказа еще не существует
        if (reviewRepository.existsByUserIdAndOrderId(reviewDto.getUserId(), reviewDto.getOrderId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ReviewDto savedReview = reviewRepository.save(reviewDto);
        return ResponseEntity.ok(savedReview);
    }

    @Operation(summary = "Get all reviews", description = "Returns a list of all reviews")
    @ApiResponse(responseCode = "200", description = "List of all reviews",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewDto.class))})
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewRepository.findAll());
    }

    @Operation(summary = "Get review by ID", description = "Returns a single review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))}),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(
            @Parameter(description = "ID of the review to retrieve", required = true)
            @PathVariable Long id) {
        ReviewDto review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return ResponseEntity.ok(review);
    }

    @Operation(summary = "Update review", description = "Updates existing review information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Review, User or Order not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(
            @Parameter(description = "ID of the review to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated review data", required = true)
            @Valid @RequestBody ReviewDto reviewDto) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    // Проверка существования пользователя и заказа
                    if (!userRepository.existsById(reviewDto.getUserId())) {
                        throw new ResourceNotFoundException("User not found with id: " + reviewDto.getUserId());
                    }

                    if (!orderRepository.existsById(reviewDto.getOrderId())) {
                        throw new ResourceNotFoundException("Order not found with id: " + reviewDto.getOrderId());
                    }

                    reviewDto.setId(id);
                    return ResponseEntity.ok(reviewRepository.save(reviewDto));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    @Operation(summary = "Delete review", description = "Deletes a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the review to delete", required = true)
            @PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get reviews by user", description = "Returns all reviews by a specific user")
    @ApiResponse(responseCode = "200", description = "List of user's reviews",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewDto.class))})
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByUser(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(reviewRepository.findByUserId(userId));
    }

    @Operation(summary = "Get reviews by order", description = "Returns all reviews for a specific order")
    @ApiResponse(responseCode = "200", description = "List of reviews for the order",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewDto.class))})
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByOrder(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId) {
        return ResponseEntity.ok(reviewRepository.findByOrderId(orderId));
    }

    @Operation(summary = "Get reviews with minimum rating",
            description = "Returns all reviews with rating equal or higher than specified")
    @ApiResponse(responseCode = "200", description = "List of reviews with minimum rating",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewDto.class))})
    @GetMapping("/by-rating")
    public ResponseEntity<List<ReviewDto>> getReviewsByMinRating(
            @Parameter(description = "Minimum rating value (1-5)", required = true)
            @RequestParam Integer minRating) {
        if (minRating < 1 || minRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return ResponseEntity.ok(reviewRepository.findByMinRating(minRating));
    }
}