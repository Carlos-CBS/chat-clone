package com.carlos.Chat.Service;

import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carlos.Chat.Models.chat.Chat;
import com.carlos.Chat.Models.chat.ChatResponse;
import com.carlos.Chat.Models.message.MessageResponse;
import com.carlos.Chat.Models.user.User;
import com.carlos.Chat.Repository.ChatRepository;
import com.carlos.Chat.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userReposisoty;
    private final ChatMapper mapper;

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(OAuth2User currentUser) {
        final String userId = currentUser.getAttribute("sub");
        return chatRepository.findChatsBySenderId(userId)
        .stream()
        .map(c -> mapper.toChatResponse(c, userId))
        .toList();
    }

    public String createChat(String senderId, String receiverId) {
        Optional<Chat> existingChat = chatRepository.findChatByRecieverAndSender(senderId, receiverId);
        if (existingChat.isPresent()) {
            return existingChat.get().getId();
        }
        User sender = userReposisoty.findById(senderId)
        .orElseThrow(() -> new EntityNotFoundException("User with id " + senderId + " not found"));

        User receiver = userReposisoty.findById(receiverId)
        .orElseThrow(() -> new EntityNotFoundException("User with id " + receiverId + " not found"));

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(receiver);

        Chat savedChat = chatRepository.save(chat);
        return savedChat.getId();
    }
}
