package com.example.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.dto.ArticleQuery;
import com.example.blog.dto.ArticleSaveRequest;
import com.example.blog.vo.ArticleDetailVO;
import com.example.blog.vo.ArticleSummaryVO;
import java.util.List;
import java.util.Map;

public interface ArticleService {
    IPage<ArticleSummaryVO> pagePublicArticles(ArticleQuery query);
    ArticleDetailVO getPublicArticle(Long id);
    List<ArticleSummaryVO> featuredPublicArticles();
    List<ArticleSummaryVO> latestPublicArticles(int limit);
    List<ArticleSummaryVO> hotPublicArticles(int limit);
    Map<String, ArticleSummaryVO> adjacentPublicArticles(Long id);
    IPage<ArticleSummaryVO> pageAdminArticles(ArticleQuery query);
    IPage<ArticleSummaryVO> pageLikedArticles(Long userId, long page, long size);
    ArticleDetailVO getAdminArticle(Long id);
    ArticleDetailVO createArticle(ArticleSaveRequest request, Long authorId);
    ArticleDetailVO updateArticle(Long id, ArticleSaveRequest request);
    void deleteArticle(Long id);
}
