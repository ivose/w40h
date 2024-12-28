package com.example.some.dto;

import lombok.Data;

@Data
public class PasswordForgotDto {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

