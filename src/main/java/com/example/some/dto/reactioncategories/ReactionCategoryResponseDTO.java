package com.example.some.dto.reactioncategories;

import lombok.Data;

@Data
public class ReactionCategoryResponseDTO {
    private Long id;
    private String name;
    private String icon;

    public ReactionCategoryResponseDTO(Long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
}