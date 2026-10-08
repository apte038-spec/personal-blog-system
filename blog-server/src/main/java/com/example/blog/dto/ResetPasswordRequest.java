package com.example.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱长度不能超过100位") String email,
        @NotBlank(message = "验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "验证码必须为6位数字") String code,
        @NotBlank(message = "新密码不能为空")
        @Size(min = 8, max = 64, message = "新密码须为8-64位") String newPassword,
        @NotBlank(message = "确认密码不能为空") String confirmPassword
) {}
