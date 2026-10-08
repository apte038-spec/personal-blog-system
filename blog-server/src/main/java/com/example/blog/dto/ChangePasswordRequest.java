package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "当前密码不能为空") String currentPassword,
        @NotBlank(message = "新密码不能为空")
        @Size(min = 8, max = 64, message = "新密码须为8-64位") String newPassword,
        @NotBlank(message = "确认密码不能为空") String confirmPassword
) {}
