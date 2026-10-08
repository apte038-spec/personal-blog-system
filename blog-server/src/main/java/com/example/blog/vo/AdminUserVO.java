package com.example.blog.vo;
import java.time.LocalDateTime;
public record AdminUserVO(Long id, String username, String nickname, String email, String avatar, String role, String status, LocalDateTime createdAt) {}
