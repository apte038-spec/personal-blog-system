package com.example.blog.service;

import com.example.blog.dto.ResetPasswordRequest;

public interface PasswordResetService {
    void sendResetCode(String email);
    void resetPassword(ResetPasswordRequest request);
}
