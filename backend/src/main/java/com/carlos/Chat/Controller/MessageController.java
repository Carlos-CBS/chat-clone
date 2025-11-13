package com.carlos.Chat.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carlos.Chat.Models.message.MessageRequest;
import com.carlos.Chat.Models.message.MessageResponse;
import com.carlos.Chat.Service.MessageService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
public class MessageController {
    
    private final MessageService messageService;

    @PostMapping("")
    public ResponseEntity<Void> saveMessage(@RequestBody MessageRequest message) {      
        messageService.saveMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadMedia(
        @RequestParam("chatId") String chatId,
        @Parameter()
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal OAuth2User currentUser
    ) {
        messageService.uploadMediaMessage(chatId, file, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> setMessagesToSeen(
        @RequestParam("chatId") String chatId,
        @AuthenticationPrincipal OAuth2User currentUser) {

        messageService.setMessageToSeen(chatId, currentUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable("chatId") String chatId) {
        return ResponseEntity.ok(messageService.findChatMessages(chatId));
    }
    
    
}
