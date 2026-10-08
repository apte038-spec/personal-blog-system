package com.example.blog.vo;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleDetailVO(Long id, String title, String slug, String summary, String content, String coverUrl,
                              Long categoryId, String categoryName, Long authorId, String authorName, String status,
                              Integer isTop, Integer viewCount, Integer likeCount, Integer commentCount,
                              LocalDateTime publishedAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                              List<TagVO> tags) {}
