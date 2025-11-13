package com.carlos.Chat.Service;

import org.springframework.stereotype.Service;

import com.carlos.Chat.Models.FileUtils;
import com.carlos.Chat.Models.message.Message;
import com.carlos.Chat.Models.message.MessageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageMapper {
    
    public MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .type(message.getType())
                .state(message.getState())
                .createdAt(message.getCreatedAt())
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .build();
    }
}
