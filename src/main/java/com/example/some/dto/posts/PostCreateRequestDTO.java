package com.example.some.dto.posts;

import lombok.Data;

@Data
public class PostCreateRequestDTO {
    private String title;
    private String content;
}
