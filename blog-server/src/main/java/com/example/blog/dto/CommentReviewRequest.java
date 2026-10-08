package com.example.blog.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CommentReviewRequest(
        @NotEmpty(message = "请选择需要审核的评论")
        @Size(max = 100, message = "一次最多审核100条评论")
        List<@NotNull(message = "评论ID不能为空") Long> ids,
        @NotBlank(message = "审核状态不能为空")
        @Pattern(regexp = "APPROVED|REJECTED", message = "审核状态只能是APPROVED或REJECTED")
        String status
) {}
