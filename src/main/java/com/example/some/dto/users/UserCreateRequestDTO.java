package com.example.some.dto.users;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserCreateRequestDTO {
    private String email;
    private String username;
    private String password;
    private String fullname;
    private LocalDate born;
}
