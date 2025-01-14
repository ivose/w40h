package com.example.some.dto.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailsResponseDTO extends UserResponseDTO {
    private boolean isAdmin;
    private LocalDateTime updatedAt;
}