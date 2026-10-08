package com.example.blog.vo;

import com.example.blog.entity.User;

/** 公开给客户端的用户资料，绝不含密码或逻辑删除字段。 */
public record UserVO(Long id, String username, String nickname, String email, String avatar, String role) {
    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getAvatar(), user.getRole());
    }
}
