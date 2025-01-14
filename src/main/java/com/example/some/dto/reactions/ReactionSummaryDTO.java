package com.example.some.dto.reactions;

import lombok.Data;

@Data
public class ReactionSummaryDTO {
    private Long categoryId;
    private String categoryName;
    private String icon;
    private int count;
}