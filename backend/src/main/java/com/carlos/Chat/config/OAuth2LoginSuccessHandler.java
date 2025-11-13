package com.carlos.Chat.config;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.carlos.Chat.Models.user.User;
import com.carlos.Chat.Repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, ServletException {

        setDefaultTargetUrl("http://localhost:4200/main");
        setAlwaysUseDefaultTargetUrl(true);
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String id = oAuth2User.getAttribute("sub"); // Google ID
        String email = oAuth2User.getAttribute("email");
        String givenName = oAuth2User.getAttribute("given_name");
        String familyName = oAuth2User.getAttribute("family_name");
        String picture = oAuth2User.getAttribute("picture");
        
        // Verify if exist, if not create the user.
        userRepository.findById(id).orElseGet(() -> {
            User newUser = User.builder()
                .id(id)
                .email(email)
                .name(givenName != null ? givenName : "Unknown")
                .lastName(familyName != null ? familyName : "")
                .picture(picture != null ? picture : "")
                .lastSeen(LocalDateTime.now())
                .build();
            
            return userRepository.save(newUser);
        });
        
        userRepository.findById(id).ifPresent(user -> {
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
        });
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
