package com.example.blog.service;

import com.example.blog.common.BusinessException;
import com.example.blog.dto.TagRequest;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.ArticleTagMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.service.impl.TagAdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagAdminServiceImplTest {
    @Mock TagMapper tagMapper;
    @Mock ArticleTagMapper articleTagMapper;
    @InjectMocks TagAdminServiceImpl service;

    @Test
    void createNormalizesAndActivatesTag() {
        when(tagMapper.selectOne(any())).thenReturn(null);
        service.create(new TagRequest(" Java ", "java", " 后端 ", "ACTIVE"));
        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagMapper).insert(captor.capture());
        assertEquals("Java", captor.getValue().getName());
        assertEquals("后端", captor.getValue().getDescription());
        assertEquals("ACTIVE", captor.getValue().getStatus());
    }

    @Test
    void referencedTagCannotBeDeleted() {
        Tag tag = new Tag(); tag.setId(4L);
        when(tagMapper.selectById(4L)).thenReturn(tag);
        when(articleTagMapper.selectCount(any())).thenReturn(1L);
        assertThrows(BusinessException.class, () -> service.delete(4L));
        verify(tagMapper, never()).deleteById(4L);
    }
}
