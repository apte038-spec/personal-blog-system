package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.ContentRequest;
import com.example.blog.security.AuthPrincipal;
import com.example.blog.service.InteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InteractionController {
    private final InteractionService interactionService;

    @GetMapping("/api/public/articles/{articleId}/comments")
    public Result<?> comments(@PathVariable Long articleId) {
        return Result.success(interactionService.approvedComments(articleId));
    }

    @PostMapping("/api/articles/{articleId}/comments")
    public Result<Void> comment(@PathVariable Long articleId, @AuthenticationPrincipal AuthPrincipal principal,
                                @Valid @RequestBody ContentRequest request) {
        interactionService.createComment(articleId, principal.id(), request.content());
        return Result.success();
    }

    @GetMapping("/api/articles/{articleId}/like")
    public Result<?> likeState(@PathVariable Long articleId, @AuthenticationPrincipal AuthPrincipal principal) {
        return Result.success(interactionService.likeState(articleId, principal.id()));
    }

    @PostMapping("/api/articles/{articleId}/like")
    public Result<Void> like(@PathVariable Long articleId, @AuthenticationPrincipal AuthPrincipal principal) {
        interactionService.like(articleId, principal.id());
        return Result.success();
    }

    @DeleteMapping("/api/articles/{articleId}/like")
    public Result<Void> unlike(@PathVariable Long articleId, @AuthenticationPrincipal AuthPrincipal principal) {
        interactionService.unlike(articleId, principal.id());
        return Result.success();
    }

    @GetMapping("/api/public/messages")
    public Result<?> messages(@RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "20") long size) {
        return Result.success(interactionService.approvedMessages(page, size));
    }

    @PostMapping("/api/messages")
    public Result<Void> message(@AuthenticationPrincipal AuthPrincipal principal, @Valid @RequestBody ContentRequest request) {
        interactionService.createMessage(principal.id(), request.content());
        return Result.success();
    }
}
