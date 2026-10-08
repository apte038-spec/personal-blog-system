package com.example.blog.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("blog_category") public class Category { @TableId(type=IdType.AUTO) private Long id; private String name,description,status; private Integer sortOrder; private LocalDateTime createdAt,updatedAt; @TableLogic private Integer deleted; }
