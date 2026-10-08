package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ArticleMapper extends BaseMapper<Article> {
    /** 绕过 MyBatis Plus 逻辑删除过滤，用于校验数据库唯一键。 */
    @Select("SELECT id FROM blog_article WHERE slug = #{slug} LIMIT 1")
    Long selectIdBySlugIncludingDeleted(@Param("slug") String slug);
}
