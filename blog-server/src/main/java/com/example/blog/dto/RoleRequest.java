package com.example.blog.dto;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
public record RoleRequest(@NotNull(message = "角色不能为空") @Pattern(regexp = "ADMIN|USER", message = "角色只能是 ADMIN 或 USER") String role) {}
