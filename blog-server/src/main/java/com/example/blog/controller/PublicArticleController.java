package com.example.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.common.Result;
import com.example.blog.dto.ArticleQuery;
import com.example.blog.service.ArticleService;
import com.example.blog.vo.ArticleDetailVO;
import com.example.blog.vo.ArticleSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/articles")
@RequiredArgsConstructor
public class PublicArticleController {
    private final ArticleService articleService;

    /** 支持 keyword、categoryId、tagId、page、size 查询参数；仅返回已发布文章。 */
    @GetMapping
    public Result<IPage<ArticleSummaryVO>> page(ArticleQuery query) {
        return Result.success(articleService.pagePublicArticles(query));
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getPublicArticle(id));
    }

    /** 兼容首页置顶文章展示。 */
    @GetMapping("/featured")
    public Result<List<ArticleSummaryVO>> featured() {
        return Result.success(articleService.featuredPublicArticles());
    }

    @GetMapping("/hot")
    public Result<List<ArticleSummaryVO>> hot(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(articleService.hotPublicArticles(limit));
    }

    @GetMapping("/latest")
    public Result<List<ArticleSummaryVO>> latest(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(articleService.latestPublicArticles(limit));
    }

    /** 兼容文章详情页的上一篇、下一篇导航。 */
    @GetMapping("/adjacent/{id}")
    public Result<Map<String, ArticleSummaryVO>> adjacent(@PathVariable Long id) {
        return Result.success(articleService.adjacentPublicArticles(id));
    }
}
