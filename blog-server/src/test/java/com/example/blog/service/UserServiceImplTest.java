package com.example.blog.service;

import com.example.blog.common.BusinessException;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.ChangePasswordRequest;
import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import com.example.blog.security.JwtService;
import com.example.blog.service.impl.UserServiceImpl;
import com.example.blog.service.AvatarStorageService;
import com.example.blog.vo.AvatarVO;
import com.example.blog.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AvatarStorageService avatarStorageService;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void registerCreatesAnActiveNormalUserWithEncryptedPassword() {
        when(userMapper.selectByUsernameIncludingDeleted("新用户1")).thenReturn(null);
        when(userMapper.selectByEmailIncludingDeleted("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt-value");
        org.mockito.Mockito.doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        UserVO result = userService.register(new RegisterRequest("新用户1", "password123", "password123", "new@example.com"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertEquals("USER", captor.getValue().getRole());
        assertEquals("ACTIVE", captor.getValue().getStatus());
        assertEquals("bcrypt-value", captor.getValue().getPassword());
        assertEquals("新用户1", captor.getValue().getNickname());
        assertEquals("new@example.com", captor.getValue().getEmail());
        assertEquals(100L, result.id());
    }

    @Test
    void registerRejectsMismatchedPasswordsBeforeWriting() {
        assertThrows(BusinessException.class, () -> userService.register(
                new RegisterRequest("new_user", "password123", "different123", "new@example.com")));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void disabledUserWithCorrectPasswordGetsExplicitMessage() {
        User user = new User();
        user.setUsername("disabled_user");
        user.setPassword("bcrypt-value");
        user.setStatus("DISABLED");
        user.setDeleted(0);
        when(userMapper.selectByUsernameIncludingDeleted("disabled_user")).thenReturn(user);
        when(passwordEncoder.matches("password123", "bcrypt-value")).thenReturn(true);

        BusinessException error = assertThrows(BusinessException.class,
                () -> userService.login(new LoginRequest("disabled_user", "password123")));

        assertEquals("该账号已被禁用，暂时无法登录", error.getMessage());
        verify(jwtService, never()).create(any(User.class));
    }

    @Test
    void changePasswordEncryptsPasswordAndInvalidatesOldTokens() {
        User user = new User();
        user.setId(3L); user.setPassword("old-hash"); user.setTokenVersion(2);
        when(userMapper.selectActiveById(3L)).thenReturn(user);
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        userService.changePassword(3L, new ChangePasswordRequest("old-password", "new-password", "new-password"));

        assertEquals("new-hash", user.getPassword());
        assertEquals(3, user.getTokenVersion());
        verify(userMapper).updateById(user);
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() {
        User user = new User(); user.setId(3L); user.setPassword("old-hash");
        when(userMapper.selectActiveById(3L)).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", "old-hash")).thenReturn(false);
        assertThrows(BusinessException.class, () -> userService.changePassword(3L,
                new ChangePasswordRequest("wrong-password", "new-password", "new-password")));
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void updateAvatarChangesOnlyTheCurrentUsersAvatar() {
        User user = new User(); user.setId(8L); user.setUsername("user8"); user.setAvatar(null);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[]{1});
        when(userMapper.selectActiveById(8L)).thenReturn(user);
        when(avatarStorageService.store(file)).thenReturn(
                new AvatarStorageService.StoredAvatar("/uploads/avatars/00000000-0000-0000-0000-000000000008.png"));
        when(userMapper.updateById(user)).thenReturn(1);

        AvatarVO result = userService.updateAvatar(8L, file);

        assertEquals("/uploads/avatars/00000000-0000-0000-0000-000000000008.png", result.avatar());
        assertEquals(result.avatar(), user.getAvatar());
        verify(userMapper).updateById(user);
    }
}
