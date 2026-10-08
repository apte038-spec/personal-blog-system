package com.example.blog.dto;

import lombok.Data;

@Data
public class ArticleQuery {
    private long page = 1;
    private long size = 10;
    private String keyword;
    private Long categoryId;
    private Long tagId;
    private String status;
}
