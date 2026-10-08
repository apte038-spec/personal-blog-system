package com.example.blog.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record ContentRequest(@NotBlank(message = "内容不能为空") @Size(max = 500, message = "内容不能超过500字") String content) {}
