package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BusinessException;
import com.example.blog.entity.*;
import com.example.blog.mapper.*;
import com.example.blog.service.InteractionService;
import com.example.blog.vo.CommentVO;
import com.example.blog.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public List<CommentVO> approvedComments(Long articleId) {
        requirePublishedArticle(articleId);
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId).eq(Comment::getStatus, "APPROVED")
                        .orderByDesc(Comment::getCreatedAt))
                .stream().map(this::commentVO).toList();
    }

    @Override
    @Transactional
    public void createComment(Long articleId, Long userId, String content) {
        requirePublishedArticle(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        comment.setStatus("PENDING");
        comment.setDeleted(0);
        try {
            commentMapper.insert(comment);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("请勿重复提交相同评论");
        }
    }

    @Override
    public Map<String, Boolean> likeState(Long articleId, Long userId) {
        requirePublishedArticle(articleId);
        long count = articleLikeMapper.selectCount(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId).eq(ArticleLike::getUserId, userId));
        return Map.of("liked", count > 0);
    }

    @Override
    @Transactional
    public void like(Long articleId, Long userId) {
        requirePublishedArticle(articleId);
        if (articleLikeMapper.selectCount(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId).eq(ArticleLike::getUserId, userId)) > 0) return;
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        try {
            articleLikeMapper.insert(like);
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, articleId).setSql("like_count = like_count + 1"));
        } catch (DuplicateKeyException ignored) {
            // 并发重复点赞由联合主键保证幂等。
        }
    }

    @Override
    @Transactional
    public void unlike(Long articleId, Long userId) {
        requirePublishedArticle(articleId);
        int deleted = articleLikeMapper.delete(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId).eq(ArticleLike::getUserId, userId));
        if (deleted > 0) {
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, articleId).gt(Article::getLikeCount, 0)
                    .setSql("like_count = like_count - 1"));
        }
    }

    @Override
    public IPage<MessageVO> approvedMessages(long page, long size) {
        Page<Message> source = messageMapper.selectPage(new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 100)),
                new LambdaQueryWrapper<Message>().eq(Message::getStatus, "APPROVED").orderByDesc(Message::getCreatedAt));
        Page<MessageVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(this::messageVO).toList());
        return result;
    }

    @Override
    public void createMessage(Long userId, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setContent(content.trim());
        message.setStatus("PENDING");
        message.setDeleted(0);
        try {
            messageMapper.insert(message);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("请勿重复提交相同留言");
        }
    }

    private Article requirePublishedArticle(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || !"PUBLISHED".equals(article.getStatus())) throw new BusinessException("文章不存在");
        return article;
    }

    private CommentVO commentVO(Comment comment) {
        User user = userMapper.selectById(comment.getUserId());
        return new CommentVO(comment.getId(), comment.getContent(), comment.getUserId(),
                user == null ? "已注销用户" : user.getNickname(), user == null ? null : user.getAvatar(), comment.getCreatedAt());
    }

    private MessageVO messageVO(Message message) {
        User user = userMapper.selectById(message.getUserId());
        return new MessageVO(message.getId(), message.getContent(), message.getUserId(),
                user == null ? "已注销用户" : user.getNickname(), user == null ? null : user.getAvatar(), message.getCreatedAt());
    }
}
