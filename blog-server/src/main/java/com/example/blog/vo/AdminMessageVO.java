package com.example.blog.vo;

import java.time.LocalDateTime;

public record AdminMessageVO(Long id, Long userId, String username, String nickname, String avatar,
                             String content, String status, LocalDateTime createdAt) {}
