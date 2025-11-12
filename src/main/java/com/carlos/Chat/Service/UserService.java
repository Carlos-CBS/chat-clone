package com.carlos.Chat.Service;

import java.util.List;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.carlos.Chat.Models.user.UserMapper;
import com.carlos.Chat.Models.user.UserResponse;
import com.carlos.Chat.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsersExceptSelf(OAuth2User connectedUser) {
        return userRepository.findAllUserExceptSelf(connectedUser.getAttribute("sub"))
        .stream()
        .map(userMapper::toUserResponse)
        .toList();
    }
    
}
