package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank(message = "标签名称不能为空") @Size(max = 50, message = "标签名称不能超过50个字符") String name,
        @NotBlank(message = "标签别名不能为空")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "标签别名仅支持小写字母、数字和连字符")
        @Size(max = 80, message = "标签别名不能超过80个字符") String slug,
        @Size(max = 255, message = "标签描述不能超过255个字符") String description,
        @Pattern(regexp = "ACTIVE|INACTIVE", message = "标签状态不正确") String status
) {}
