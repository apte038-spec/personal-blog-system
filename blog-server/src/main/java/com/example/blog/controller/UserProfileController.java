package com.example.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.common.Result;
import com.example.blog.dto.UpdateProfileRequest;
import com.example.blog.dto.ChangePasswordRequest;
import com.example.blog.security.AuthPrincipal;
import com.example.blog.service.ArticleService;
import com.example.blog.service.UserService;
import com.example.blog.vo.ArticleSummaryVO;
import com.example.blog.vo.UserVO;
import com.example.blog.vo.AvatarVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserService userService;
    private final ArticleService articleService;

    @PutMapping
    public Result<UserVO> updateProfile(@AuthenticationPrincipal AuthPrincipal principal,
                                        @Valid @RequestBody UpdateProfileRequest request) {
        return Result.success(userService.updateProfile(principal.id(), request));
    }

    @PostMapping("/avatar")
    public Result<AvatarVO> updateAvatar(@AuthenticationPrincipal AuthPrincipal principal,
                                         @RequestPart("file") MultipartFile file) {
        return Result.success("头像修改成功", userService.updateAvatar(principal.id(), file));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@AuthenticationPrincipal AuthPrincipal principal,
                                       @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.id(), request);
        return Result.success();
    }

    @GetMapping("/likes")
    public Result<IPage<ArticleSummaryVO>> likedArticles(@AuthenticationPrincipal AuthPrincipal principal,
                                                         @RequestParam(defaultValue = "1") long page,
                                                         @RequestParam(defaultValue = "6") long size) {
        return Result.success(articleService.pageLikedArticles(principal.id(), page, size));
    }
}
