package com.example.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.dto.MessageReviewRequest;
import com.example.blog.vo.AdminMessageVO;
import java.util.Map;

public interface MessageAdminService {
    IPage<AdminMessageVO> page(long page, long size, String status, String keyword);
    void review(MessageReviewRequest request);
    Map<String, Long> stats();
}
