package com.carlos.Chat.Models.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .lastName(user.getLastName())
            .lastSeen(user.getLastSeen())
            .email(user.getEmail())
            .isOnline(user.isUserOnline())
            .build();
    }
}
