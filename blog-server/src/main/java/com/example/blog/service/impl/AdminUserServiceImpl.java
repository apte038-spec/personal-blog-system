package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BusinessException;
import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.AdminUserService;
import com.example.blog.vo.AdminUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserMapper userMapper;

    @Override
    public IPage<AdminUserVO> page(long page, long size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .and(keyword != null && !keyword.isBlank(), w -> w.like(User::getUsername, keyword)
                        .or().like(User::getNickname, keyword).or().like(User::getEmail, keyword))
                .orderByDesc(User::getCreatedAt);
        Page<User> source = userMapper.selectPage(new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 100)), wrapper);
        Page<AdminUserVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(this::toVO).toList());
        return result;
    }

    @Override public long count() { return userMapper.selectCount(null); }

    @Override
    public void updateRole(Long id, String role, Long operatorId) {
        if (id.equals(operatorId)) throw new BusinessException("不能修改当前登录账号的权限");
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setRole(role);
        userMapper.updateById(user);
    }

    @Override
    public void updateStatus(Long id, String status, Long operatorId) {
        if (id.equals(operatorId) && "DISABLED".equals(status)) {
            throw new BusinessException("不能禁用当前登录账号");
        }
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
    }

    private AdminUserVO toVO(User user) {
        return new AdminUserVO(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), user.getAvatar(),
                user.getRole(), user.getStatus(), user.getCreatedAt());
    }
}
