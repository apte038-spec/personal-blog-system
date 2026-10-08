package com.example.blog.vo;

import com.example.blog.entity.Tag;

public record TagVO(Long id, String name, String slug) {
    public static TagVO from(Tag tag) { return new TagVO(tag.getId(), tag.getName(), tag.getSlug()); }
}
