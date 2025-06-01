package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class OrderDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    @NotNull(message = "Order date is required")
    @Column(name = "orderdate", nullable = false)
    private LocalDate orderDate = LocalDate.now();

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false)
    private String status;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Column(name = "userid", nullable = false)
    private Long userId;

    // Конструкторы
    public OrderDto() {
    }

    public OrderDto(String status, Long userId) {
        this.status = status;
        this.userId = userId;
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

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Методы equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return id != null && id.equals(orderDto.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Метод toString
    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", version=" + version +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", userId=" + userId +
                '}';
    }
}