package com.example.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.vo.AdminUserVO;

public interface AdminUserService {
    IPage<AdminUserVO> page(long page, long size, String keyword);
    long count();
    void updateRole(Long id, String role, Long operatorId);
    void updateStatus(Long id, String status, Long operatorId);
}
