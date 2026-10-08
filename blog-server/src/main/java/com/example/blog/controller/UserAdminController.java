package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.RoleRequest;
import com.example.blog.dto.UserStatusRequest;
import com.example.blog.security.AuthPrincipal;
import com.example.blog.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final AdminUserService adminUserService;

    @GetMapping
    public Result<?> users(@RequestParam(defaultValue = "1") long page,
                           @RequestParam(defaultValue = "10") long size,
                           @RequestParam(required = false) String keyword) {
        return Result.success(adminUserService.page(page, size, keyword));
    }

    @GetMapping("/stats")
    public Result<?> stats() { return Result.success(Map.of("total", adminUserService.count())); }

    @PatchMapping("/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id, @AuthenticationPrincipal AuthPrincipal principal,
                                   @Valid @RequestBody RoleRequest request) {
        adminUserService.updateRole(id, request.role(), principal.id());
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @AuthenticationPrincipal AuthPrincipal principal,
                                     @Valid @RequestBody UserStatusRequest request) {
        adminUserService.updateStatus(id, request.status(), principal.id());
        return Result.success();
    }
}
