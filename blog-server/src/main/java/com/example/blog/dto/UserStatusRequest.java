package com.example.blog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserStatusRequest(
        @NotNull(message = "用户状态不能为空")
        @Pattern(regexp = "ACTIVE|DISABLED", message = "用户状态只能是 ACTIVE 或 DISABLED")
        String status
) {}
