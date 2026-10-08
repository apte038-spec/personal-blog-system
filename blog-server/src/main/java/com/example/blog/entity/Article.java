package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_article")
public class Article {
    @TableId(type = IdType.AUTO) private Long id;
    private String title, slug, summary, content, coverUrl, status;
    private Long categoryId, authorId;
    private Integer isTop, viewCount, likeCount, commentCount;
    private LocalDateTime publishedAt, createdAt, updatedAt;
    @TableLogic private Integer deleted;
}
