package com.example.some.dto;

import lombok.Data;
import java.util.Date;

@Data
public class SuccessMessageDTO {

    public SuccessMessageDTO(String name) {
        this.name = name;
        this.statusCode = 200;
        this.date = new Date();
    }

    private String name;
    private Date date;;
    private int statusCode;
}
