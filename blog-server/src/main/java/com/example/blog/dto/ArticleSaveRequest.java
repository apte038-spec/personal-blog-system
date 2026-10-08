package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ArticleSaveRequest(
        @NotBlank(message = "标题不能为空") @Size(max = 200, message = "标题不能超过200字") String title,
        @NotBlank(message = "文章别名不能为空") @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "文章别名仅支持小写字母、数字和连字符") @Size(max = 220, message = "文章别名不能超过220位") String slug,
        @Size(max = 500, message = "摘要不能超过500字") String summary,
        @NotBlank(message = "正文不能为空") String content,
        @Size(max = 500, message = "封面地址不能超过500字") String coverUrl,
        @NotNull(message = "请选择文章分类") Long categoryId,
        @NotBlank(message = "请选择文章状态") @Pattern(regexp = "^(DRAFT|PUBLISHED)$", message = "文章状态不正确") String status,
        Integer isTop,
        List<Long> tagIds
) {}
