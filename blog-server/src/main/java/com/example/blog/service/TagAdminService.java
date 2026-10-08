package com.example.blog.service;

import com.example.blog.dto.TagRequest;
import com.example.blog.entity.Tag;
import java.util.List;

public interface TagAdminService {
    List<Tag> list();
    Tag create(TagRequest request);
    Tag update(Long id, TagRequest request);
    void delete(Long id);
}
