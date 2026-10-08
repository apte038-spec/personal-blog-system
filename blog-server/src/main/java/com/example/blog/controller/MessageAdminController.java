package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.MessageReviewRequest;
import com.example.blog.service.MessageAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class MessageAdminController {
    private final MessageAdminService messageAdminService;

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(defaultValue = "PENDING") String status,
                          @RequestParam(required = false) String keyword) {
        return Result.success(messageAdminService.page(page, size, status, keyword));
    }

    @GetMapping("/stats") public Result<?> stats() { return Result.success(messageAdminService.stats()); }
    @PatchMapping("/status") public Result<Void> review(@Valid @RequestBody MessageReviewRequest request) {
        messageAdminService.review(request);
        return Result.success();
    }
}
