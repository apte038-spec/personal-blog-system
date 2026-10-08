package com.example.blog.security;

/** 已写入 Spring Security 上下文的当前认证用户。 */
public record AuthPrincipal(Long id, String username, String role) {}
