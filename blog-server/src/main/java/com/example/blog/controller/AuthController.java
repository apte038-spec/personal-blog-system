package com.example.blog.controller;

import com.example.blog.common.Result;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.dto.SendResetCodeRequest;
import com.example.blog.dto.ResetPasswordRequest;
import com.example.blog.security.AuthPrincipal;
import com.example.blog.service.UserService;
import com.example.blog.service.PasswordResetService;
import com.example.blog.vo.LoginVO;
import com.example.blog.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/password/reset-code")
    public Result<Void> sendPasswordResetCode(@Valid @RequestBody SendResetCodeRequest request) {
        passwordResetService.sendResetCode(request.email());
        return Result.success();
    }

    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return Result.success();
    }

    @GetMapping({"/me", "/profile"})
    public Result<UserVO> getCurrentUser(@AuthenticationPrincipal AuthPrincipal principal) {
        return Result.success(userService.getCurrentUser(principal.id()));
    }
}
