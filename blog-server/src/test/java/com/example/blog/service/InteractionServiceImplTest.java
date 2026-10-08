package com.example.blog.service;

import com.example.blog.entity.Message;
import com.example.blog.mapper.*;
import com.example.blog.service.impl.InteractionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InteractionServiceImplTest {
    @Mock ArticleMapper articleMapper;
    @Mock CommentMapper commentMapper;
    @Mock ArticleLikeMapper articleLikeMapper;
    @Mock MessageMapper messageMapper;
    @Mock UserMapper userMapper;
    @InjectMocks InteractionServiceImpl service;

    @Test
    void newMessageWaitsForReview() {
        service.createMessage(8L, "  课程设计加油  ");
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageMapper).insert(captor.capture());
        assertEquals("PENDING", captor.getValue().getStatus());
        assertEquals("课程设计加油", captor.getValue().getContent());
        assertEquals(8L, captor.getValue().getUserId());
    }
}
