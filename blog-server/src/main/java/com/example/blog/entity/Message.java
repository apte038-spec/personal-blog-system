package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_message")
public class Message {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic private Integer deleted;
}
