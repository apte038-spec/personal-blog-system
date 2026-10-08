package com.example.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.common.Result;
import com.example.blog.dto.ArticleQuery;
import com.example.blog.dto.ArticleSaveRequest;
import com.example.blog.security.AuthPrincipal;
import com.example.blog.service.ArticleService;
import com.example.blog.vo.ArticleDetailVO;
import com.example.blog.vo.ArticleSummaryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleAdminController {
    private final ArticleService articleService;

    @GetMapping
    public Result<IPage<ArticleSummaryVO>> page(ArticleQuery query) {
        return Result.success(articleService.pageAdminArticles(query));
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getAdminArticle(id));
    }

    @PostMapping
    public Result<ArticleDetailVO> create(@Valid @RequestBody ArticleSaveRequest request,
                                          @AuthenticationPrincipal AuthPrincipal principal) {
        return Result.success(articleService.createArticle(request, principal.id()));
    }

    @PutMapping("/{id}")
    public Result<ArticleDetailVO> update(@PathVariable Long id, @Valid @RequestBody ArticleSaveRequest request) {
        return Result.success(articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }
}
