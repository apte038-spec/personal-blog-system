package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BusinessException;
import com.example.blog.dto.CommentReviewRequest;
import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.CommentAdminService;
import com.example.blog.vo.AdminCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {
    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<AdminCommentVO> page(long page, long size, String status, String keyword) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(status != null && !status.isBlank(), Comment::getStatus, status)
                .like(keyword != null && !keyword.isBlank(), Comment::getContent, keyword == null ? null : keyword.trim())
                .orderByDesc(Comment::getCreatedAt);
        IPage<Comment> source = commentMapper.selectPage(
                new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 100)), wrapper);
        Page<AdminCommentVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(this::toVO).toList());
        return result;
    }

    @Override
    @Transactional
    public void review(CommentReviewRequest request) {
        List<Long> ids = new LinkedHashSet<>(request.ids()).stream().toList();
        List<Comment> comments = commentMapper.selectBatchIds(ids);
        if (comments.size() != ids.size()) {
            throw new BusinessException("部分评论不存在或已被删除，请刷新后重试");
        }
        for (Comment comment : comments) {
            String previous = comment.getStatus();
            String target = request.status();
            if (target.equals(previous)) continue;

            comment.setStatus(target);
            commentMapper.updateById(comment);
            if (!"APPROVED".equals(previous) && "APPROVED".equals(target)) {
                articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                        .eq(Article::getId, comment.getArticleId())
                        .setSql("comment_count = comment_count + 1"));
            } else if ("APPROVED".equals(previous) && !"APPROVED".equals(target)) {
                articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                        .eq(Article::getId, comment.getArticleId())
                        .setSql("comment_count = GREATEST(comment_count - 1, 0)"));
            }
        }
    }

    @Override
    public Map<String, Long> stats() {
        return Map.of(
                "total", commentMapper.selectCount(null),
                "pending", count("PENDING"),
                "approved", count("APPROVED"),
                "rejected", count("REJECTED")
        );
    }

    private long count(String status) {
        return commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, status));
    }

    private AdminCommentVO toVO(Comment comment) {
        Article article = articleMapper.selectById(comment.getArticleId());
        User user = userMapper.selectById(comment.getUserId());
        return new AdminCommentVO(comment.getId(), comment.getArticleId(),
                article == null ? "文章已删除" : article.getTitle(), comment.getUserId(),
                user == null ? "已注销用户" : user.getUsername(),
                user == null ? "已注销用户" : user.getNickname(),
                user == null ? null : user.getAvatar(), comment.getContent(), comment.getStatus(), comment.getCreatedAt());
    }
}
