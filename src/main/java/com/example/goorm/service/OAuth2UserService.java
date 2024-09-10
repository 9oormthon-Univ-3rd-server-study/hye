package com.example.goorm.service;

import com.example.goorm.common.exception.MyErrorCode;
import com.example.goorm.common.exception.MyException;
import com.example.goorm.domain.User;
import com.example.goorm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> att = oAuth2User.getAttributes();
        for(Map.Entry<String,Object> entry : att.entrySet()){
            log.info(entry.getKey());
            log.info(entry.getValue().toString());
        }

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");



        String id = oAuth2User.getAttributes().get("id").toString();
        Map<String,Object> nick =  (Map<String, Object>) oAuth2User.getAttributes().get("properties");
        String nickname = nick.get("nickname").toString();

        if(!userRepository.existsById(id)){
            userRepository.save(User.builder().id(id).roles(Collections.singletonList("ROLE_USER")).nickname(nickname).build());
        }

        return new DefaultOAuth2User(authorities,oAuth2User.getAttributes(),"id");

    }
}

