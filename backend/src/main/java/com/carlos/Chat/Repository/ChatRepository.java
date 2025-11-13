package com.carlos.Chat.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carlos.Chat.Models.chat.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    @Query("SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId OR c.recipient.id = :senderId ORDER BY createdAt DESC")
    List<Chat> findChatsBySenderId(@Param("senderId") String userId);

    @Query("SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :recipientId) OR (c.sender.id = :recipientId AND c.recipient.id = :senderId)")
    Optional<Chat> findChatByRecieverAndSender(@Param("senderId") String senderId, @Param("recipientId") String recieverId);
}
