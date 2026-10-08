package com.example.blog.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("blog_site_config") public class SiteConfig { @TableId(type=IdType.AUTO) private Long id; private String configKey,configValue,description; private LocalDateTime updatedAt; }
