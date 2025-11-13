package com.carlos.Chat.Models.chat;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.LastModifiedDate;

import com.carlos.Chat.Models.message.MessageState;
import com.carlos.Chat.Models.message.MessageType;
import com.carlos.Chat.Models.user.User;
import com.carlos.Chat.Models.message.Message;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdAt DESC")
    private List<Message> messages;

    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime LastModifiedDate;

    @PrePersist
    private void PrePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Transient
    public String getChatName(final String senderId) {
        if (recipient.getId().equals(senderId)) {
            return sender.getName() + " " + sender.getLastName();
        }
      return recipient.getName() + " " + recipient.getLastName();  
    } 

    @Transient
    public long getUnreadMessages(final String senderId) {
        return messages.stream()
        .filter(m -> m.getReceiverId().equals(senderId))
        .filter(m -> MessageState.SENT == m.getState())
        .count();
    }

    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (messages.get(0).getType() != MessageType.TEXT) {
                return "Attachment";
            }
            return messages.get(0).getContent();
        }   
        return null;
    }
    
    @Transient
    public LocalDateTime getLastMessageTime() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).getCreatedAt();
        }
        return null;
    }

    @Transient
    public String getTargetChatName(final String senderId) {
        if (sender.getId().equals(senderId)) {
            return sender.getName() + " " + sender.getLastName();
        }
      return recipient.getName() + " " + recipient.getLastName();  
    } 
}
