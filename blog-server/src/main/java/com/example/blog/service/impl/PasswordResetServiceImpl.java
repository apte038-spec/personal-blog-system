package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.blog.common.BusinessException;
import com.example.blog.dto.ResetPasswordRequest;
import com.example.blog.entity.EmailVerificationCode;
import com.example.blog.entity.User;
import com.example.blog.mapper.EmailVerificationCodeMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.EmailService;
import com.example.blog.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final String RESET_PASSWORD = "RESET_PASSWORD";
    private static final int CODE_VALID_MINUTES = 5;
    private static final int SEND_INTERVAL_SECONDS = 60;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserMapper userMapper;
    private final EmailVerificationCodeMapper codeMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public void sendResetCode(String rawEmail) {
        String email = normalizeEmail(rawEmail);
        User user = requireRegisteredUser(email);
        requireActive(user);

        EmailVerificationCode latest = codeMapper.selectOne(new LambdaQueryWrapper<EmailVerificationCode>()
                .eq(EmailVerificationCode::getEmail, email)
                .eq(EmailVerificationCode::getType, RESET_PASSWORD)
                .orderByDesc(EmailVerificationCode::getCreateTime)
                .last("LIMIT 1"));
        LocalDateTime now = LocalDateTime.now();
        if (latest != null && latest.getCreateTime() != null
                && latest.getCreateTime().isAfter(now.minusSeconds(SEND_INTERVAL_SECONDS))) {
            throw new BusinessException("验证码发送过于频繁，请60秒后再试");
        }

        codeMapper.update(null, new UpdateWrapper<EmailVerificationCode>()
                .eq("email", email).eq("type", RESET_PASSWORD).eq("used", 0).set("used", 1));

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setType(RESET_PASSWORD);
        verificationCode.setExpireTime(now.plusMinutes(CODE_VALID_MINUTES));
        verificationCode.setUsed(0);
        verificationCode.setCreateTime(now);
        if (codeMapper.insert(verificationCode) != 1) throw new BusinessException("验证码保存失败，请稍后重试");
        emailService.sendPasswordResetCode(email, code);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        String email = normalizeEmail(request.email());
        User user = requireRegisteredUser(email);
        requireActive(user);

        EmailVerificationCode verificationCode = codeMapper.selectOne(new LambdaQueryWrapper<EmailVerificationCode>()
                .eq(EmailVerificationCode::getEmail, email)
                .eq(EmailVerificationCode::getType, RESET_PASSWORD)
                .eq(EmailVerificationCode::getCode, request.code())
                .orderByDesc(EmailVerificationCode::getCreateTime)
                .last("LIMIT 1"));
        if (verificationCode == null) throw new BusinessException("验证码错误");
        if (Integer.valueOf(1).equals(verificationCode.getUsed())) throw new BusinessException("验证码已使用");
        if (verificationCode.getExpireTime() == null || !verificationCode.getExpireTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        int consumed = codeMapper.update(null, new UpdateWrapper<EmailVerificationCode>()
                .eq("id", verificationCode.getId()).eq("used", 0).set("used", 1));
        if (consumed != 1) throw new BusinessException("验证码已使用，请重新获取");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        if (userMapper.updateById(user) != 1) throw new BusinessException("密码更新失败，请稍后重试");
    }

    private User requireRegisteredUser(String email) {
        User user = userMapper.selectByEmailIncludingDeleted(email);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new BusinessException("该邮箱未注册");
        }
        return user;
    }

    private void requireActive(User user) {
        if ("DISABLED".equals(user.getStatus())) throw new BusinessException("该账号已被禁用，无法重置密码");
        if (!"ACTIVE".equals(user.getStatus())) throw new BusinessException("用户状态异常，无法重置密码");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
