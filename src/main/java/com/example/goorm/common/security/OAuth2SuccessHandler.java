package com.example.goorm.common.security;

import com.example.goorm.domain.User;
import com.example.goorm.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Optional<User> user = userRepository.findById(authentication.getName());
        String token = tokenProvider.createToken(user.get().getId(), user.get().getRoles(), LocalDateTime.now());
        String refreshToken = tokenProvider.createRefreshToken(user.get().getId(),LocalDateTime.now());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = "{\"token\": \"" + token + "\", \"refreshToken\": \"" + refreshToken + "\"}";

        response.getWriter().write(responseBody);
        response.getWriter().flush();
    }
}
