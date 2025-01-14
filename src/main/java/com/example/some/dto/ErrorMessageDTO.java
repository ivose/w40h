package com.example.some.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ErrorMessageDTO {
    private String name;
    private Date date;
    private int statusCode;
}
