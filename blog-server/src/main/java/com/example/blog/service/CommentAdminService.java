package com.example.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.dto.CommentReviewRequest;
import com.example.blog.vo.AdminCommentVO;

import java.util.Map;

public interface CommentAdminService {
    IPage<AdminCommentVO> page(long page, long size, String status, String keyword);
    void review(CommentReviewRequest request);
    Map<String, Long> stats();
}
