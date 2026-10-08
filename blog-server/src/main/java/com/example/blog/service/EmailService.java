package com.example.blog.service;

public interface EmailService {
    void sendPasswordResetCode(String recipient, String code);
}
