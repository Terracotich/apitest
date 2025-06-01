package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Явное указание колонки
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    // Уберите аннотацию @Id с поля key, если она есть
    @Column(name = "key", unique = true) // Просто обычное поле
    private Long key;

    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name must be less than 30 characters")
    @Column(name = "firstname", nullable = false)
    private String firstName;

    @NotBlank(message = "Surname is required")
    @Size(max = 30, message = "Surname must be less than 30 characters")
    @Column(name = "surname", nullable = false)
    private String surName;

    @Size(max = 30, message = "Last name must be less than 30 characters")
    @Column(name = "lastname")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Invalid phone number format")
    @Column(name = "phonenumber", nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    @Column(name = "clientlogin", nullable = false, unique = true)
    private String clientLogin;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "clientpassword", nullable = false)
    private String clientPassword;

    @NotNull(message = "Role ID is required")
    @Column(name = "roleid", nullable = false)
    private Integer roleId;

    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate = LocalDateTime.now();

    // Геттеры
    public Long getKey() {
        return key;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurName() {
        return surName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    // Сеттеры
    public void setKey(Long key) {
        this.key = key;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setClientLogin(String clientLogin) {
        this.clientLogin = clientLogin;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public void setVersion(int i) {
    }

    public void setId(Object o) {
    }

    public Long getId() {
        return id;
    }
}