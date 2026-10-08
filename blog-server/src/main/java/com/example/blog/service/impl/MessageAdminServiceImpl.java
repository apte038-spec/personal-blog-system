package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BusinessException;
import com.example.blog.dto.MessageReviewRequest;
import com.example.blog.entity.Message;
import com.example.blog.entity.User;
import com.example.blog.mapper.MessageMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.MessageAdminService;
import com.example.blog.vo.AdminMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageAdminServiceImpl implements MessageAdminService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<AdminMessageVO> page(long page, long size, String status, String keyword) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(status != null && !status.isBlank(), Message::getStatus, status)
                .like(keyword != null && !keyword.isBlank(), Message::getContent, keyword == null ? null : keyword.trim())
                .orderByDesc(Message::getCreatedAt);
        IPage<Message> source = messageMapper.selectPage(new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 100)), wrapper);
        Page<AdminMessageVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(this::toVO).toList());
        return result;
    }

    @Override
    @Transactional
    public void review(MessageReviewRequest request) {
        List<Long> ids = new LinkedHashSet<>(request.ids()).stream().toList();
        List<Message> messages = messageMapper.selectBatchIds(ids);
        if (messages.size() != ids.size()) throw new BusinessException("部分留言不存在或已被删除，请刷新后重试");
        for (Message message : messages) {
            if (!request.status().equals(message.getStatus())) {
                message.setStatus(request.status());
                messageMapper.updateById(message);
            }
        }
    }

    @Override
    public Map<String, Long> stats() {
        return Map.of("total", messageMapper.selectCount(null), "pending", count("PENDING"),
                "approved", count("APPROVED"), "rejected", count("REJECTED"));
    }

    private long count(String status) {
        return messageMapper.selectCount(new LambdaQueryWrapper<Message>().eq(Message::getStatus, status));
    }

    private AdminMessageVO toVO(Message message) {
        User user = userMapper.selectById(message.getUserId());
        return new AdminMessageVO(message.getId(), message.getUserId(), user == null ? "已注销用户" : user.getUsername(),
                user == null ? "已注销用户" : user.getNickname(), user == null ? null : user.getAvatar(),
                message.getContent(), message.getStatus(), message.getCreatedAt());
    }
}
