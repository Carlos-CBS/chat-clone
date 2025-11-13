package com.carlos.Chat.Repository;

import org.springframework.stereotype.Repository;

import com.carlos.Chat.Models.chat.Chat;
import com.carlos.Chat.Models.message.Message;
import com.carlos.Chat.Models.message.MessageState;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{
    
    List<Message> findMessagesByChatId(String chatId);

    @Modifying
    @Query("UPDATE Message m SET m.state = :newState WHERE m.chat.id = :chatId")
    void setMessageToSeenByChatId(@Param("chatId") String chat, @Param("newState") MessageState seen);
}
