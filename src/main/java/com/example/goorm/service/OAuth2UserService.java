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
        log.info("로그인 한 플랫폼 : " + userRequest.getClientRegistration().getRegistrationId());
        /*for(Map.Entry<String,Object> entry : att.entrySet()){
            log.info(entry.getKey());
            log.info(entry.getValue().toString());
        }*/

        String id = "";
        String nickname ="";
        String nameAttributeKey = "";
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            id = oAuth2User.getAttributes().get("sub").toString();
            nickname = oAuth2User.getAttributes().get("name").toString();
            nameAttributeKey = "sub";
        }
        else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            id = oAuth2User.getAttributes().get("id").toString();
            Map<String,Object> nick =  (Map<String, Object>) oAuth2User.getAttributes().get("properties");
            nickname = nick.get("nickname").toString();
            nameAttributeKey = "id";
        }

        if(!userRepository.existsById(id)){
            userRepository.save(User.builder().id(id).roles(Collections.singletonList("ROLE_USER")).nickname(nickname).build());
        }
        Optional<User> user = userRepository.findById(id);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.get().getRoles());

        return new DefaultOAuth2User(authorities,oAuth2User.getAttributes(),nameAttributeKey);

    }
}

