package com.example.blog.vo;
import java.time.LocalDateTime;
public record MessageVO(Long id, String content, Long userId, String nickname, String avatar, LocalDateTime createdAt) {}
