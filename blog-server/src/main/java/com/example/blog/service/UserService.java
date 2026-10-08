package com.example.blog.service;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.dto.UpdateProfileRequest;
import com.example.blog.dto.ChangePasswordRequest;
import com.example.blog.vo.LoginVO;
import com.example.blog.vo.UserVO;
import com.example.blog.vo.AvatarVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserVO register(RegisterRequest request);
    LoginVO login(LoginRequest request);
    UserVO getCurrentUser(Long userId);
    UserVO updateProfile(Long userId, UpdateProfileRequest request);
    AvatarVO updateAvatar(Long userId, MultipartFile file);
    void changePassword(Long userId, ChangePasswordRequest request);
}
