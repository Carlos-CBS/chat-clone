package com.carlos.Chat.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carlos.Chat.Models.chat.ChatResponse;
import com.carlos.Chat.Models.chat.StringResponse;
import com.carlos.Chat.Service.ChatService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {
    
    private final ChatService chatService;

    @PostMapping("")
    public ResponseEntity<StringResponse> create(
        @RequestParam(name = "senderId") String senderId,
        @RequestParam(name = "receiverId") String receiverId
    ) {
        final String chatId = chatService.createChat(senderId, receiverId);   
        StringResponse res = StringResponse.builder()
        .response(chatId).build();

        return ResponseEntity.ok(res);
    }

    @GetMapping("")
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(@AuthenticationPrincipal OAuth2User user) {
        return ResponseEntity.ok(chatService.getChatsByReceiverId(user));
    }
    
    
}
