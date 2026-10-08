package com.example.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog.common.BusinessException;
import com.example.blog.common.Result;
import com.example.blog.dto.CategoryRequest;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.SiteConfig;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.SiteConfigMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/** 保留分类、站点配置和控制台接口；文章 REST 接口见 ArticleAdminController。 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final SiteConfigMapper siteConfigMapper;

    @GetMapping("/categories") public Result<?> categories() { return Result.success(categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder))); }
    @PostMapping("/categories") public Result<?> createCategory(@Valid @RequestBody CategoryRequest request) { Category category = category(request, new Category()); categoryMapper.insert(category); return Result.success(category); }
    @PutMapping("/categories/{id}") public Result<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) { Category category = categoryMapper.selectById(id); if (category == null) throw new BusinessException("分类不存在"); categoryMapper.updateById(category(request, category)); return Result.success(category); }
    @DeleteMapping("/categories/{id}") public Result<Void> deleteCategory(@PathVariable Long id) { if (articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, id)) > 0) throw new BusinessException("该分类下存在文章，不能删除"); categoryMapper.deleteById(id); return Result.success(); }
    @GetMapping("/site-config") public Result<?> configs() { return Result.success(siteConfigMapper.selectList(null)); }
    @PutMapping("/site-config") public Result<Void> saveConfigs(@RequestBody Map<String, String> values) { values.forEach((key, value) -> { SiteConfig config = siteConfigMapper.selectOne(new LambdaQueryWrapper<SiteConfig>().eq(SiteConfig::getConfigKey, key)); if (config == null) { config = new SiteConfig(); config.setConfigKey(key); config.setDescription(key); siteConfigMapper.insert(config); } config.setConfigValue(value); siteConfigMapper.updateById(config); }); return Result.success(); }
    @GetMapping("/dashboard/overview") public Result<?> overview() { return Result.success(Map.of("articleCount", articleMapper.selectCount(null), "categoryCount", categoryMapper.selectCount(null), "publishedCount", articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, "PUBLISHED")), "draftCount", articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, "DRAFT")))); }
    @GetMapping("/dashboard/publish-trend") public Result<?> trend() { List<String> dates = IntStream.rangeClosed(0, 6).mapToObj(i -> LocalDate.now().minusDays(6 - i).toString()).toList(); List<Long> counts = dates.stream().map(date -> articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, "PUBLISHED").apply("DATE(published_at) = {0}", date))).toList(); return Result.success(Map.of("dates", dates, "counts", counts)); }
    private Category category(CategoryRequest request, Category category) { category.setName(request.name()); category.setDescription(request.description()); category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder()); category.setStatus("INACTIVE".equals(request.status()) ? "INACTIVE" : "ACTIVE"); return category; }
}
