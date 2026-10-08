package com.example.blog.vo;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleSummaryVO(Long id, String title, String slug, String summary, String coverUrl,
                               Long categoryId, String categoryName, String authorName, String status,
                               Integer isTop, Integer viewCount, Integer likeCount, Integer commentCount,
                               LocalDateTime publishedAt, LocalDateTime createdAt, List<TagVO> tags) {}
