package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 当前用户修改自己的公开资料。 */
public record UpdateProfileRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 50, message = "用户名不能超过50个字符")
        String username
) {}
