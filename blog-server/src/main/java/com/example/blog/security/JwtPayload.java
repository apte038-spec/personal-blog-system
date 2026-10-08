package com.example.blog.security;

/** JWT 中经过签名保护的认证声明。 */
public record JwtPayload(Long userId, String username, String role, Integer tokenVersion) {}
