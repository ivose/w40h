package com.example.some.dto;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String password;
    private String token;

    public PasswordResetDto(String password, String token) {
        this.password = password;
        this.token = token;
    }

    public PasswordResetDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
