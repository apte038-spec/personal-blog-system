package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_article_tag")
public class ArticleTag {
    private Long articleId;
    private Long tagId;
    private LocalDateTime createdAt;
}
