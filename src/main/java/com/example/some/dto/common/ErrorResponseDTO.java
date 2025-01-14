package com.example.some.dto.common;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String message;
    private String details;
    private String timestamp;
}