package com.example.some.dto.posts;

import lombok.Data;

@Data
public class PostUpdateRequestDTO {
    private String title;
    private String content;
}