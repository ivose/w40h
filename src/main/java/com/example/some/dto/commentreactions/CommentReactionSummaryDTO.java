package com.example.some.dto.commentreactions;

import lombok.Data;

@Data
public class CommentReactionSummaryDTO {
    private Long categoryId;
    private String categoryName;
    private String icon;
    private int count;
}