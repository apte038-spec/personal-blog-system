package com.example.blog.service;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarStorageService {
    StoredAvatar store(MultipartFile file);
    void deleteManagedAvatar(String avatarPath);

    record StoredAvatar(String publicPath) {}
}
