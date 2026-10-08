package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_article_like")
public class ArticleLike {
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;
}
