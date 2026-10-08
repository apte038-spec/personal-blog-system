package com.example.blog.service;

import com.example.blog.dto.MessageReviewRequest;
import com.example.blog.entity.Message;
import com.example.blog.mapper.MessageMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.impl.MessageAdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageAdminServiceImplTest {
    @Mock MessageMapper messageMapper;
    @Mock UserMapper userMapper;
    @InjectMocks MessageAdminServiceImpl service;

    @Test
    void batchReviewUpdatesEverySelectedMessage() {
        Message first = new Message(); first.setId(1L); first.setStatus("PENDING");
        Message second = new Message(); second.setId(2L); second.setStatus("REJECTED");
        when(messageMapper.selectBatchIds(List.of(1L, 2L))).thenReturn(List.of(first, second));
        service.review(new MessageReviewRequest(List.of(1L, 2L), "APPROVED"));
        assertEquals("APPROVED", first.getStatus());
        assertEquals("APPROVED", second.getStatus());
        verify(messageMapper, times(2)).updateById(any(Message.class));
    }

    @Test
    void repeatedReviewIsIdempotent() {
        Message message = new Message(); message.setId(1L); message.setStatus("APPROVED");
        when(messageMapper.selectBatchIds(List.of(1L))).thenReturn(List.of(message));
        service.review(new MessageReviewRequest(List.of(1L), "APPROVED"));
        verify(messageMapper, never()).updateById(any(Message.class));
    }
}
