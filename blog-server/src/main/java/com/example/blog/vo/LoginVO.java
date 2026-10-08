package com.example.blog.vo;

public record LoginVO(String accessToken, String tokenType, long expiresIn, UserVO user) {}
