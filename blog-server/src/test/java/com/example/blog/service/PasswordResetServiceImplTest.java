package com.example.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.blog.common.BusinessException;
import com.example.blog.dto.ResetPasswordRequest;
import com.example.blog.entity.EmailVerificationCode;
import com.example.blog.entity.User;
import com.example.blog.mapper.EmailVerificationCodeMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {
    @Mock UserMapper userMapper;
    @Mock EmailVerificationCodeMapper codeMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;
    @InjectMocks PasswordResetServiceImpl service;

    @Test
    void sendCodeCreatesSixDigitsWithFiveMinuteExpiryAndSendsMail() {
        when(userMapper.selectByEmailIncludingDeleted("user@example.com")).thenReturn(activeUser());
        when(codeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(codeMapper.insert(any(EmailVerificationCode.class))).thenReturn(1);

        LocalDateTime before = LocalDateTime.now();
        service.sendResetCode(" User@Example.com ");

        ArgumentCaptor<EmailVerificationCode> captor = ArgumentCaptor.forClass(EmailVerificationCode.class);
        verify(codeMapper).insert(captor.capture());
        EmailVerificationCode saved = captor.getValue();
        assertTrue(saved.getCode().matches("\\d{6}"));
        assertEquals("RESET_PASSWORD", saved.getType());
        assertEquals(0, saved.getUsed());
        assertTrue(saved.getExpireTime().isAfter(before.plusMinutes(4)));
        verify(emailService).sendPasswordResetCode("user@example.com", saved.getCode());
    }

    @Test
    void sendCodeRejectsRequestWithinSixtySeconds() {
        when(userMapper.selectByEmailIncludingDeleted("user@example.com")).thenReturn(activeUser());
        EmailVerificationCode latest = new EmailVerificationCode();
        latest.setCreateTime(LocalDateTime.now().minusSeconds(20));
        when(codeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(latest);
        BusinessException error = assertThrows(BusinessException.class, () -> service.sendResetCode("user@example.com"));
        assertTrue(error.getMessage().contains("60秒"));
        verify(emailService, never()).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void resetPasswordConsumesCodeAndStoresBcryptValue() {
        User user = activeUser();
        when(userMapper.selectByEmailIncludingDeleted("user@example.com")).thenReturn(user);
        EmailVerificationCode code = validCode();
        when(codeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(code);
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("bcrypt-new");
        when(codeMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1);
        when(userMapper.updateById(user)).thenReturn(1);

        service.resetPassword(new ResetPasswordRequest("user@example.com", "123456", "new-password", "new-password"));

        assertEquals("bcrypt-new", user.getPassword());
        verify(userMapper).updateById(user);
    }

    @Test
    void resetPasswordRejectsExpiredCode() {
        when(userMapper.selectByEmailIncludingDeleted("user@example.com")).thenReturn(activeUser());
        EmailVerificationCode code = validCode(); code.setExpireTime(LocalDateTime.now().minusSeconds(1));
        when(codeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(code);
        BusinessException error = assertThrows(BusinessException.class, () -> service.resetPassword(
                new ResetPasswordRequest("user@example.com", "123456", "new-password", "new-password")));
        assertTrue(error.getMessage().contains("过期"));
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void resetPasswordRejectsUsedCode() {
        when(userMapper.selectByEmailIncludingDeleted("user@example.com")).thenReturn(activeUser());
        EmailVerificationCode code = validCode(); code.setUsed(1);
        when(codeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(code);
        BusinessException error = assertThrows(BusinessException.class, () -> service.resetPassword(
                new ResetPasswordRequest("user@example.com", "123456", "new-password", "new-password")));
        assertTrue(error.getMessage().contains("已使用"));
    }

    @Test
    void disabledAccountCannotSendOrReset() {
        User user = activeUser(); user.setStatus("DISABLED");
        when(userMapper.selectByEmailIncludingDeleted("user@example.com")).thenReturn(user);
        BusinessException error = assertThrows(BusinessException.class, () -> service.sendResetCode("user@example.com"));
        assertTrue(error.getMessage().contains("禁用"));
        verify(codeMapper, never()).insert(any(EmailVerificationCode.class));
    }

    private User activeUser() {
        User user = new User();
        user.setId(10L); user.setEmail("user@example.com"); user.setPassword("old-hash");
        user.setStatus("ACTIVE"); user.setDeleted(0);
        return user;
    }

    private EmailVerificationCode validCode() {
        EmailVerificationCode code = new EmailVerificationCode();
        code.setId(9L); code.setEmail("user@example.com"); code.setCode("123456");
        code.setType("RESET_PASSWORD"); code.setUsed(0); code.setExpireTime(LocalDateTime.now().plusMinutes(3));
        code.setCreateTime(LocalDateTime.now());
        return code;
    }
}
