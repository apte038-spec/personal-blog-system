package com.example.blog.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record MessageReviewRequest(
        @NotEmpty(message = "请选择需要审核的留言")
        @Size(max = 100, message = "一次最多审核100条留言")
        List<@NotNull(message = "留言ID不能为空") Long> ids,
        @NotBlank(message = "审核状态不能为空")
        @Pattern(regexp = "APPROVED|REJECTED", message = "审核状态只能是APPROVED或REJECTED") String status
) {}
