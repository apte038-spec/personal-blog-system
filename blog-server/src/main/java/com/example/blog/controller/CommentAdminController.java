package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.CommentReviewRequest;
import com.example.blog.service.CommentAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentAdminService commentAdminService;

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(defaultValue = "PENDING") String status,
                          @RequestParam(required = false) String keyword) {
        return Result.success(commentAdminService.page(page, size, status, keyword));
    }

    @PatchMapping("/status")
    public Result<Void> review(@Valid @RequestBody CommentReviewRequest request) {
        commentAdminService.review(request);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<?> stats() {
        return Result.success(commentAdminService.stats());
    }
}
