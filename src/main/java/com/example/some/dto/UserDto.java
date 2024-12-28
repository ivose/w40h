package com.example.some.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String username;
    private LocalDate born;
    private boolean isActive;
    private boolean isAdmin;
    private boolean isClient;

    public UserDto() {
    }

    public UserDto(Long id, String email, String fullName, String username, LocalDate born, boolean isActive,
                   boolean isAdmin, boolean isClient) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.born = born;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.isClient = isClient;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBorn() {
        return born;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean client) {
        isClient = client;
    }


}
