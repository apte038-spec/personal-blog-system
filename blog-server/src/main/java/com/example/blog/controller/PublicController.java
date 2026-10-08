package com.example.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog.common.Result;
import com.example.blog.entity.Category;
import com.example.blog.entity.SiteConfig;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.SiteConfigMapper;
import com.example.blog.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

/** 保留公开分类和站点资料接口；文章接口见 PublicArticleController。 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final CategoryMapper categoryMapper;
    private final SiteConfigMapper siteConfigMapper;
    private final TagMapper tagMapper;
    @GetMapping("/categories") public Result<?> categories() { return Result.success(categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getStatus, "ACTIVE").orderByAsc(Category::getSortOrder))); }
    @GetMapping("/tags") public Result<?> tags() { return Result.success(tagMapper.selectList(new LambdaQueryWrapper<Tag>().eq(Tag::getStatus, "ACTIVE").orderByAsc(Tag::getName))); }
    @GetMapping("/site-config") public Result<?> siteConfig() { return Result.success(siteConfigMapper.selectList(null).stream().collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue))); }
}
