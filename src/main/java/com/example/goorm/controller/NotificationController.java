package com.example.goorm.controller;

import com.example.goorm.domain.User;
import com.example.goorm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sub(/*@AuthenticationPrincipal User user*/ @RequestParam String userId /*테스트를 위해 유저 아이디를 파라미터로 받음*/){
        User user = User.builder().id(userId).build();// 테스트를 위해 임의 유저 객체 생성
        return ResponseEntity.ok().body(notificationService.addEmitter(user));
    }

    //쪽지 기능이 있다는 가정 하에 쪽지 전송 시 상대방에게 알림이 가게 한다
    @PostMapping("")
    public ResponseEntity<SseEmitter> send(@AuthenticationPrincipal User user,@RequestBody Map<String,String> body /* 쪽지 dto */ ){
        /*
        쪽지 발송 관련 메소드
        그 이후 수신자에게 알림을 보낸다
         */
        log.info("발신자 id :{} ",user.getId());
        notificationService.sendEvent(user, body.get("userId"));
        return ResponseEntity.ok().build();
    }
}
