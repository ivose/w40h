package com.example.some.dto.reactioncategories;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ReactionCategoryCreateRequestDTO {
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 20, message = "Name cannot exceed 20 characters")
    private String name;

    @Size(max = 50, message = "Icon cannot exceed 50 characters")
    private String icon;
}