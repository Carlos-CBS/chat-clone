package com.carlos.Chat.Service;

import org.springframework.stereotype.Service;

import com.carlos.Chat.Models.chat.Chat;
import com.carlos.Chat.Models.chat.ChatResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMapper {
    public ChatResponse toChatResponse(Chat c, String senderId)  {
        return ChatResponse.builder()
            .id(c.getId())
            .name(c.getChatName(senderId))
            .unreadCount(c.getUnreadMessages(senderId))
            .lastMessage(c.getLastMessage())
            .lastMessageTime(c.getLastMessageTime())
            .isRecipientOnline(c.getRecipient().isUserOnline())
            .senderId(c.getSender().getId())
            .receiverId(c.getRecipient().getId())
            .build();
    }   

}
