package com.example.blog.service.impl;

import com.example.blog.common.BusinessException;
import com.example.blog.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    @Value("${blog.mail.from:}")
    private String from;

    @Override
    public void sendPasswordResetCode(String recipient, String code) {
        if (from == null || from.isBlank()) {
            throw new BusinessException("邮件服务尚未配置，请联系管理员");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("个人博客密码重置验证码");
        message.setText("您正在重置博客账号密码。\n\n验证码：" + code
                + "\n\n验证码5分钟内有效，请勿将验证码泄露给其他人。\n如非本人操作，请忽略此邮件。");
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            log.error("密码重置验证码邮件发送失败，recipient={}", recipient, exception);
            throw new BusinessException("验证码发送失败，请稍后重试或联系管理员");
        }
    }
}
