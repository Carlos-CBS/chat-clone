package com.carlos.Chat.Service;

import java.util.List;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.carlos.Chat.Models.FileUtils;
import com.carlos.Chat.Models.Notification.Notification;
import com.carlos.Chat.Models.Notification.NotificationType;
import com.carlos.Chat.Models.chat.Chat;
import com.carlos.Chat.Models.message.MessageRequest;
import com.carlos.Chat.Models.message.MessageResponse;
import com.carlos.Chat.Models.message.MessageState;
import com.carlos.Chat.Models.message.MessageType;
import com.carlos.Chat.Models.message.Message;
import com.carlos.Chat.Repository.ChatRepository;
import com.carlos.Chat.Repository.MessageRepository;
import com.carlos.Chat.ws.NotificationService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(messageRequest.getChatId())
        .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Message message = Message.builder()
        .content(messageRequest.getContent())
        .chat(chat)
        .senderId(messageRequest.getSenderId())
        .receiverId(messageRequest.getReceiverId())
        .type(messageRequest.getType())
        .state(MessageState.SENT).build();

        messageRepository.save(message);

        Notification notification = Notification.builder()
        .chatId(chat.getId())
        .messageType(messageRequest.getType())
        .content(messageRequest.getContent())
        .senderId(messageRequest.getSenderId())
        .receiverId(messageRequest.getReceiverId())
        .type(NotificationType.MESSAGE)
        .chatName(chat.getTargetChatName(message.getSenderId()))
        .build();

        notificationService.sendNotification(message.getReceiverId(), notification);
        
    }

    public List<MessageResponse> findChatMessages(String chatId){
        return messageRepository.findMessagesByChatId(chatId)
        .stream()
        .map(mapper::toMessageResponse).toList();
    }

    @Transactional
    public void setMessageToSeen(String chatId, OAuth2User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        String recipientId = getRecipientId(chat, currentUser);

        messageRepository.setMessageToSeenByChatId(chatId, MessageState.SEEN);

        Notification notification = Notification.builder()
        .chatId(chat.getId())
        .senderId(getSenderId(chat, currentUser))
        .receiverId(recipientId)
        .type(NotificationType.SEEN)
        .build();

        notificationService.sendNotification(recipientId, notification);
        
    }

    public void uploadMediaMessage(String chatId, MultipartFile file, OAuth2User currentUser) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        final String senderId = getSenderId(chat, currentUser);
        final String recipientId = getRecipientId(chat, currentUser);
        
        final String filePath = fileService.saveFile(file, senderId);

        Message message = Message.builder()
        .chat(chat)
        .senderId(senderId)
        .receiverId(recipientId)
        .type(MessageType.IMAGE)
        .state(MessageState.SENT)
        .mediaFilePath(filePath)
        .content("Attachment")
        .build();

        messageRepository.save(message);

        Notification notification = Notification.builder()
        .chatId(chat.getId())
        .messageType(MessageType.IMAGE)
        .senderId(senderId)
        .receiverId(recipientId)
        .type(NotificationType.IMAGE)
        .media(FileUtils.readFileFromLocation(filePath))
        .build();

        notificationService.sendNotification(recipientId, notification);
        
    }

    public String getSenderId(Chat chat, OAuth2User currentUser) {
        if (chat.getSender().getId().equals(currentUser.getAttribute("sub"))) {
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }

    public String getRecipientId(Chat chat, OAuth2User currentUser) {

        if (chat.getSender().getId().equals(currentUser.getAttribute("sub"))) {
            return chat.getRecipient().getId();
        }

        return chat.getSender().getId();
    }
}
