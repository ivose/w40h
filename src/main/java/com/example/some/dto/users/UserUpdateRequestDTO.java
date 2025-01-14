package com.example.some.dto.users;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserUpdateRequestDTO {
    private String email;
    private String fullname;
    private LocalDate born;
    private String currentPassword;
    private String newPassword;
}
