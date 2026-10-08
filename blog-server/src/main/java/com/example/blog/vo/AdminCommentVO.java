package com.example.blog.vo;

import java.time.LocalDateTime;

public record AdminCommentVO(
        Long id,
        Long articleId,
        String articleTitle,
        Long userId,
        String username,
        String nickname,
        String avatar,
        String content,
        String status,
        LocalDateTime createdAt
) {}
