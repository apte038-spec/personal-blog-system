package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_tag")
public class Tag {
    @TableId(type = IdType.AUTO) private Long id;
    private String name, slug, description, status;
    private LocalDateTime createdAt, updatedAt;
    @TableLogic private Integer deleted;
}
