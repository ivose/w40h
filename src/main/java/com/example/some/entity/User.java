package com.example.some.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
//import com.cgi.library.model.Role;

@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String resetToken;

    private Long resetTokenExpiry;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate born;

    /**
     * The new schema has is_active NOT NULL DEFAULT FALSE
     */
    private boolean isActive;

    /**
     * The new schema has is_admin NOT NULL DEFAULT FALSE
     */
    private boolean isAdmin;

    /**
     * The new schema has is_client NOT NULL DEFAULT FALSE
     */
    private boolean isClient;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public User() {
    }

    public User(Long id, String email, String username, String password, String resetToken,
                Long resetTokenExpiry, String fullName, LocalDate born, boolean isActive,
                boolean isAdmin, boolean isClient, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.resetToken = resetToken;
        this.resetTokenExpiry = resetTokenExpiry;
        this.fullName = fullName;
        this.born = born;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.isClient = isClient;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public Long getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBorn() {
        return born;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isClient() {
        return isClient;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setResetTokenExpiry(Long resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setClient(boolean client) {
        isClient = client;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
