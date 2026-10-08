package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_comment")
public class Comment {
    @TableId(type = IdType.AUTO) private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic private Integer deleted;
}
