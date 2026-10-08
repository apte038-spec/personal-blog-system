package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.common.BusinessException;
import com.example.blog.dto.ArticleQuery;
import com.example.blog.dto.ArticleSaveRequest;
import com.example.blog.entity.Article;
import com.example.blog.entity.ArticleLike;
import com.example.blog.entity.ArticleTag;
import com.example.blog.entity.Category;
import com.example.blog.entity.Tag;
import com.example.blog.entity.User;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.ArticleLikeMapper;
import com.example.blog.mapper.ArticleTagMapper;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.ArticleService;
import com.example.blog.vo.ArticleDetailVO;
import com.example.blog.vo.ArticleSummaryVO;
import com.example.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<ArticleSummaryVO> pagePublicArticles(ArticleQuery query) {
        LambdaQueryWrapper<Article> wrapper = baseQuery(query)
                .eq(Article::getStatus, "PUBLISHED")
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getPublishedAt);
        return toSummaryPage(articleMapper.selectPage(pageOf(query), wrapper));
    }

    @Override
    @Transactional
    public ArticleDetailVO getPublicArticle(Long id) {
        Article article = getRequiredArticle(id, true);
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id).setSql("view_count = view_count + 1"));
        article.setViewCount(article.getViewCount() + 1);
        return toDetail(article);
    }

    @Override
    public List<ArticleSummaryVO> featuredPublicArticles() {
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, "PUBLISHED").eq(Article::getIsTop, 1)
                        .orderByDesc(Article::getPublishedAt).last("LIMIT 3"))
                .stream().map(this::toSummary).toList();
    }

    @Override
    public List<ArticleSummaryVO> latestPublicArticles(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, "PUBLISHED")
                        .orderByDesc(Article::getPublishedAt)
                        .last("LIMIT " + safeLimit))
                .stream().map(this::toSummary).toList();
    }

    @Override
    public List<ArticleSummaryVO> hotPublicArticles(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, "PUBLISHED")
                        .orderByDesc(Article::getViewCount)
                        .orderByDesc(Article::getLikeCount)
                        .orderByDesc(Article::getPublishedAt)
                        .last("LIMIT " + safeLimit))
                .stream().map(this::toSummary).toList();
    }

    @Override
    public Map<String, ArticleSummaryVO> adjacentPublicArticles(Long id) {
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "PUBLISHED").orderByAsc(Article::getPublishedAt));
        int index = java.util.stream.IntStream.range(0, articles.size())
                .filter(i -> Objects.equals(articles.get(i).getId(), id)).findFirst().orElse(-1);
        Map<String, ArticleSummaryVO> result = new HashMap<>();
        result.put("previous", index > 0 ? toSummary(articles.get(index - 1)) : null);
        result.put("next", index >= 0 && index < articles.size() - 1 ? toSummary(articles.get(index + 1)) : null);
        return result;
    }

    @Override
    public IPage<ArticleSummaryVO> pageAdminArticles(ArticleQuery query) {
        return toSummaryPage(articleMapper.selectPage(pageOf(query), baseQuery(query).orderByDesc(Article::getUpdatedAt)));
    }

    @Override
    public IPage<ArticleSummaryVO> pageLikedArticles(Long userId, long page, long size) {
        List<Long> ids = articleLikeMapper.selectList(new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getUserId, userId))
                .stream().map(ArticleLike::getArticleId).toList();
        if (ids.isEmpty()) {
            return new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 100), 0);
        }
        IPage<Article> source = articleMapper.selectPage(
                new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 100)),
                new LambdaQueryWrapper<Article>().in(Article::getId, ids)
                        .eq(Article::getStatus, "PUBLISHED")
                        .orderByDesc(Article::getPublishedAt));
        return toSummaryPage(source);
    }

    @Override
    public ArticleDetailVO getAdminArticle(Long id) {
        return toDetail(getRequiredArticle(id, false));
    }

    @Override
    @Transactional
    public ArticleDetailVO createArticle(ArticleSaveRequest request, Long authorId) {
        validateSaveRequest(request, null);
        Article article = new Article();
        apply(article, request);
        article.setAuthorId(authorId);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setDeleted(0);
        articleMapper.insert(article);
        replaceTags(article.getId(), request.tagIds());
        return getAdminArticle(article.getId());
    }

    @Override
    @Transactional
    public ArticleDetailVO updateArticle(Long id, ArticleSaveRequest request) {
        Article article = getRequiredArticle(id, false);
        validateSaveRequest(request, id);
        apply(article, request);
        articleMapper.updateById(article);
        replaceTags(id, request.tagIds());
        return getAdminArticle(id);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        getRequiredArticle(id, false);
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        articleMapper.deleteById(id);
    }

    private LambdaQueryWrapper<Article> baseQuery(ArticleQuery query) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .like(query.getKeyword() != null && !query.getKeyword().isBlank(), Article::getTitle, query.getKeyword())
                .eq(query.getCategoryId() != null, Article::getCategoryId, query.getCategoryId())
                .eq(query.getStatus() != null && !query.getStatus().isBlank(), Article::getStatus, query.getStatus());
        if (query.getTagId() != null) {
            List<Long> articleIds = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                            .eq(ArticleTag::getTagId, query.getTagId()))
                    .stream().map(ArticleTag::getArticleId).toList();
            if (articleIds.isEmpty()) {
                wrapper.apply("1 = 0");
            } else {
                wrapper.in(Article::getId, articleIds);
            }
        }
        return wrapper;
    }

    private Page<Article> pageOf(ArticleQuery query) {
        return new Page<>(Math.max(1, query.getPage()), Math.min(Math.max(1, query.getSize()), 100));
    }

    private IPage<ArticleSummaryVO> toSummaryPage(IPage<Article> source) {
        Page<ArticleSummaryVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(this::toSummary).toList());
        return result;
    }

    private ArticleSummaryVO toSummary(Article article) {
        Category category = categoryMapper.selectById(article.getCategoryId());
        User author = userMapper.selectById(article.getAuthorId());
        return new ArticleSummaryVO(article.getId(), article.getTitle(), article.getSlug(), article.getSummary(), article.getCoverUrl(),
                article.getCategoryId(), category == null ? null : category.getName(), author == null ? "未知作者" : author.getNickname(),
                article.getStatus(), article.getIsTop(), article.getViewCount(), article.getLikeCount(), article.getCommentCount(),
                article.getPublishedAt(), article.getCreatedAt(), tagsOf(article.getId()));
    }

    private ArticleDetailVO toDetail(Article article) {
        Category category = categoryMapper.selectById(article.getCategoryId());
        User author = userMapper.selectById(article.getAuthorId());
        return new ArticleDetailVO(article.getId(), article.getTitle(), article.getSlug(), article.getSummary(), article.getContent(), article.getCoverUrl(),
                article.getCategoryId(), category == null ? null : category.getName(), article.getAuthorId(), author == null ? "未知作者" : author.getNickname(),
                article.getStatus(), article.getIsTop(), article.getViewCount(), article.getLikeCount(), article.getCommentCount(), article.getPublishedAt(),
                article.getCreatedAt(), article.getUpdatedAt(), tagsOf(article.getId()));
    }

    private List<TagVO> tagsOf(Long articleId) {
        List<Long> ids = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId))
                .stream().map(ArticleTag::getTagId).toList();
        return ids.isEmpty() ? Collections.emptyList() : tagMapper.selectBatchIds(ids).stream().map(TagVO::from).toList();
    }

    private Article getRequiredArticle(Long id, boolean publishedOnly) {
        Article article = articleMapper.selectById(id);
        if (article == null || publishedOnly && !"PUBLISHED".equals(article.getStatus())) {
            throw new BusinessException("文章不存在");
        }
        return article;
    }

    private void validateSaveRequest(ArticleSaveRequest request, Long currentId) {
        if (categoryMapper.selectById(request.categoryId()) == null) {
            throw new BusinessException("请选择有效分类");
        }
        Long sameSlug = articleMapper.selectIdBySlugIncludingDeleted(request.slug());
        if (sameSlug != null && !Objects.equals(sameSlug, currentId)) {
            throw new BusinessException("文章别名已被使用");
        }
        validateTagIds(request.tagIds());
    }

    private void validateTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        List<Long> uniqueIds = new LinkedHashSet<>(tagIds).stream().toList();
        List<Tag> tags = tagMapper.selectBatchIds(uniqueIds);
        if (tags.size() != uniqueIds.size() || tags.stream().anyMatch(tag -> !"ACTIVE".equals(tag.getStatus()))) {
            throw new BusinessException("包含无效或已停用标签");
        }
    }

    private void apply(Article article, ArticleSaveRequest request) {
        article.setTitle(request.title());
        article.setSlug(request.slug());
        article.setSummary(request.summary());
        article.setContent(request.content());
        article.setCoverUrl(request.coverUrl());
        article.setCategoryId(request.categoryId());
        article.setStatus(request.status());
        article.setIsTop(Objects.equals(request.isTop(), 1) ? 1 : 0);
        article.setPublishedAt("PUBLISHED".equals(request.status())
                ? article.getPublishedAt() == null ? LocalDateTime.now() : article.getPublishedAt() : null);
    }

    private void replaceTags(Long articleId, List<Long> tagIds) {
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
        if (tagIds == null) return;
        for (Long tagId : new LinkedHashSet<>(tagIds)) {
            ArticleTag relation = new ArticleTag();
            relation.setArticleId(articleId);
            relation.setTagId(tagId);
            articleTagMapper.insert(relation);
        }
    }
}
