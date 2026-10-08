package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog.common.BusinessException;
import com.example.blog.dto.TagRequest;
import com.example.blog.entity.ArticleTag;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.ArticleTagMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.service.TagAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagAdminServiceImpl implements TagAdminService {
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Override
    public List<Tag> list() {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getName));
    }

    @Override
    @Transactional
    public Tag create(TagRequest request) {
        validateUnique(request, null);
        Tag tag = apply(new Tag(), request);
        tag.setDeleted(0);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    @Transactional
    public Tag update(Long id, TagRequest request) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) throw new BusinessException("标签不存在");
        validateUnique(request, id);
        tagMapper.updateById(apply(tag, request));
        return tag;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (tagMapper.selectById(id) == null) throw new BusinessException("标签不存在");
        if (articleTagMapper.selectCount(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, id)) > 0) {
            throw new BusinessException("该标签已被文章使用，不能删除，可将其停用");
        }
        tagMapper.deleteById(id);
    }

    private void validateUnique(TagRequest request, Long currentId) {
        Tag same = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .and(w -> w.eq(Tag::getName, request.name().trim()).or().eq(Tag::getSlug, request.slug().trim()))
                .ne(currentId != null, Tag::getId, currentId).last("LIMIT 1"));
        if (same != null) throw new BusinessException("标签名称或别名已被使用");
    }

    private Tag apply(Tag tag, TagRequest request) {
        tag.setName(request.name().trim());
        tag.setSlug(request.slug().trim());
        tag.setDescription(request.description() == null ? null : request.description().trim());
        tag.setStatus("INACTIVE".equals(request.status()) ? "INACTIVE" : "ACTIVE");
        return tag;
    }
}
