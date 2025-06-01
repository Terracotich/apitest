package com.example.apidemo.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "roles")
public class RolesDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version = 0;

    @NotBlank(message = "Role title is required")
    @Size(max = 13, message = "Role title must be less than 13 characters")
    @Column(name = "charactertitle", nullable = false, unique = true)
    private String characterTitle;

    // Конструкторы
    public RolesDto() {
    }

    public RolesDto(String characterTitle) {
        this.characterTitle = characterTitle;
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

    public String getCharacterTitle() {
        return characterTitle;
    }

    public void setCharacterTitle(String characterTitle) {
        this.characterTitle = characterTitle;
    }
}