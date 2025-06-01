package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "review")
public class ReviewDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    @NotBlank(message = "Review title is required")
    @Size(max = 200, message = "Review title must be less than 200 characters")
    @Column(name = "reviewtitle", nullable = false)
    private String reviewTitle;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotNull(message = "Review date is required")
    @Column(name = "reviewdate", nullable = false)
    private LocalDate reviewDate = LocalDate.now();

    @NotNull(message = "User ID is required")
    @Column(name = "userid", nullable = false)
    private Long userId;

    @NotNull(message = "Order ID is required")
    @Column(name = "orderid", nullable = false)
    private Long orderId;

    // Конструкторы
    public ReviewDto() {
    }

    public ReviewDto(String reviewTitle, Integer rating, Long userId, Long orderId) {
        this.reviewTitle = reviewTitle;
        this.rating = rating;
        this.userId = userId;
        this.orderId = orderId;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}