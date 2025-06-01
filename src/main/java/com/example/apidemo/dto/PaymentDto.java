package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class PaymentDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotBlank(message = "Payment method is required")
    @Size(max = 10, message = "Payment method must be less than 10 characters")
    @Column(name = "paymentmethod", nullable = false)
    private String paymentMethod;

    @NotNull(message = "Payment date is required")
    @Column(name = "paymentdate", nullable = false)
    private LocalDate paymentDate = LocalDate.now();

    @NotNull(message = "User ID is required")
    @Column(name = "userid", nullable = false)
    private Long userId;

    @NotNull(message = "Order ID is required")
    @Column(name = "orderid", nullable = false)
    private Long orderId;

    // Конструкторы
    public PaymentDto() {
    }

    public PaymentDto(Integer price, String paymentMethod, Long userId, Long orderId) {
        this.price = price;
        this.paymentMethod = paymentMethod;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
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