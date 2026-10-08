package com.example.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.vo.CommentVO;
import com.example.blog.vo.MessageVO;
import java.util.List;
import java.util.Map;

public interface InteractionService {
    List<CommentVO> approvedComments(Long articleId);
    void createComment(Long articleId, Long userId, String content);
    Map<String, Boolean> likeState(Long articleId, Long userId);
    void like(Long articleId, Long userId);
    void unlike(Long articleId, Long userId);
    IPage<MessageVO> approvedMessages(long page, long size);
    void createMessage(Long userId, String content);
}
